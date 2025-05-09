from datetime import datetime, timedelta, timezone
from locust import HttpUser, events, task
from random import randint
from time import sleep
from uuid import uuid4

WORLD_LIST_SERVICE_URL = "http://host.docker.internal:8080/api/v1"
TEAM_SERVICE_URL = "http://host.docker.internal:8081/api"
GAME_SERVICE_URL = "http://host.docker.internal:8082/api/v1"

@events.init_command_line_parser.add_listener
def _(parser):
    parser.add_argument("--oauth2-token", type=str, default=None,
                        help="Please provide your OAuth2 token from oauth-client.")

class FriendlyMatchRunnableScenario(HttpUser):
    token = None
    
    @task
    def run_test(self):
        self.token = self.environment.parsed_options.oauth2_token

        if (self.token is None):
            print("Please provide OAuth2 token using --oauth2-token CLI argument")
            return
                
        print("Friendly match showcase started")
        team_predators = self.create_fictive_team(0)
        team_ducks = self.create_fictive_team(1)

        budget_before_match_predators = self.create_budget_system(team_predators["guid"])
        budget_before_match_ducks = self.create_budget_system(team_ducks["guid"])

        print("Budget of Team 0 before the match", budget_before_match_predators["amount"])
        print("Budget of Team 1 before the match", budget_before_match_ducks["amount"])

        arena = self.fetch_arena()
        match = self.create_match(arena["guid"], team_predators["guid"], team_ducks["guid"])
        (score_home_team, score_away_team) = self.get_match_result(match["guid"])

        budget_amount_after_match_predators = self.get_budget(budget_before_match_predators["guid"])["amount"]
        budget_amount_after_match_ducks = self.get_budget(budget_before_match_ducks["guid"])["amount"]

        if score_home_team == score_away_team:
            print(
                f"Match was draw! Both teams have recieved reward! New budgets: Team Predators {budget_amount_after_match_predators}, Team Ducks {budget_amount_after_match_ducks}")
        elif score_home_team > score_away_team:
            print(f"Home team won (Predators)! New budget: {budget_amount_after_match_predators}")
        else:
            print(f"Away team won (Ducks)! New budget: {budget_amount_after_match_ducks}")

        print("Friendly match showcase ended. Press Ctrl + C to terminate the app...")
        self.stop()

    def fetch_arena(self):
        arenas = self.client.get(GAME_SERVICE_URL + "/arena/", params={"page": 0, "size": 1, "sort": "arenaName,ASC"},
                                 headers=self.get_authorization_header()).json()

        print(f"Fetched arena: {str(arenas)}")
        return arenas["content"][0]

    def create_match(self, arena_id, home_team_id, away_team_id):
        tz = timezone(timedelta(hours=2))

        match = self.client.post(GAME_SERVICE_URL + "/matches/", json={
            "arenaUid": arena_id,
            "startAt": (datetime.now(tz) - timedelta(minutes=5)).isoformat(),
            "homeTeamUid": home_team_id,
            "awayTeamUid": away_team_id
        }, headers=self.get_authorization_header()).json()

        print(f"Created match for teams {home_team_id, away_team_id} in arena {arena_id}. Match ID: {match["guid"]}")
        return match

    def get_match_result(self, match_id):
        get_match = lambda: self.client.get(GAME_SERVICE_URL + f"/matches/{match_id}", params={"results": "true"},
                                            headers=self.get_authorization_header()).json()

        match = get_match()

        while ("result" not in match):
            sleep(10)
            match = get_match()
            print("Match in progress. Waiting for result of the match")

        result = match["result"]
        score_home_team = result["scoreHomeTeam"]
        score_away_team = result["scoreAwayTeam"]
        print(f"Score: Home team {score_home_team} - {score_away_team} Away team")

        return (score_home_team, score_away_team)

    def create_fictive_team(self, team_number):
        players = self.get_players(team_number)
        fictive_team = self.client.post(TEAM_SERVICE_URL + "/v1/fictive-team/", json={
            "name": "Team" + str(team_number),
            "playerIds": players,
            "characteristicType": "STRENGTH",
            "ownerId": str(uuid4())
        }, headers=self.get_authorization_header()).json()

        print(f"Created fictive team with id {fictive_team["guid"]}")
        return fictive_team

    def create_budget_system(self, team_uuid):
        """TODO: Doesn't make much sense to create budget_system by player. Will be fixed in M4."""

        budget_system = self.client.post(TEAM_SERVICE_URL + "/api/budget-systems", json={
            "amount": 1000,
            "teamId": team_uuid
        }, headers=self.get_authorization_header()).json()

        print(f"Created budget system for team {team_uuid}")
        return budget_system

    def get_budget(self, budget_system_id):
        budget_system = self.client.get(TEAM_SERVICE_URL + "/api/budget-systems/" + budget_system_id,
                                        headers=self.get_authorization_header()).json()

        return budget_system

    def create_team_characteristic(self, team_uuid):
        """TODO: Doesn't make much sense to create this in here - it should be created by allocating players from the WorldListService. Will be fixed in M4."""

        team_characteristic = self.client.post(TEAM_SERVICE_URL + "/api/team-characteristics", json={
            "amount": 1000,
            "characteristicType": "SPEED" if randint(0, 1) == 1 else "SHOOTING",
            "characteristicValue": randint(0, 99)
        }, headers=self.get_authorization_header()).json()

        print(f"Created team characteristic for team {team_uuid}")
        return team_characteristic

    def get_players(self, page=0):
        response = self.client.get(WORLD_LIST_SERVICE_URL + "/players/",
                                   params={"page": page, "size": 20, "sort": "lastName,ASC"},
                                   headers=self.get_authorization_header()).json()
        players: list = response["content"]
        return list(map(lambda player: player["id"], players))

    def get_authorization_header(self) -> dict[str, str]:
        return {"Authorization": f"Bearer {self.token}"}
