package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Service for scheduling tasks (threads). Service restarts must be handled by the client
 * - when the service gets restarted, tasks must be rescheduled manually
 */
@Service
public class TaskSchedulerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedulerService.class);

	private final TaskScheduler taskScheduler;

	private final ConcurrentHashMap<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

	@Autowired
	public TaskSchedulerService(TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	/**
	 * Schedules task for the given time. If the currentTime > startAt time, then it's
	 * started immediately
	 * @param runnable task's runnable
	 * @param taskID ID of the started task
	 * @param startAt instant defining when the task should start
	 */
	public void scheduleTask(@NotNull Runnable runnable, @NotNull UUID taskID, @NotNull Instant startAt) {
		ValidationHelper.requireNonNull(runnable, "Please provide runnable for the scheduled task");
		ValidationHelper.requireNonNull(taskID, "Please provide taskID for the scheduled task");
		ValidationHelper.requireNonNull(startAt, "Please provide startAt time for the scheduled task");

		var scheduledMatch = scheduledTasks.get(taskID);
		if (scheduledMatch != null) {
			removeAmbiguousTask(scheduledMatch, taskID);
			return;
		}

		var task = taskScheduler.schedule(wrapTaskRunnable(runnable, taskID), startAt);
		scheduledTasks.put(taskID, task);
		LOGGER.debug("Task '{}' scheduled to '{}'", taskID, startAt);
	}

	/**
	 * Cancels scheduled task
	 * @param taskID ID of the task to be canceled
	 */
	public void cancelTask(@NotNull UUID taskID) {
		ValidationHelper.requireNonNull(taskID, "Please provide taskID to cancel");

		var task = scheduledTasks.get(taskID);
		if (task == null) {
			return;
		}
		task.cancel(false);
	}

	private void removeAmbiguousTask(ScheduledFuture<?> task, UUID taskID) {
		switch (task.state()) {
			case FAILED, SUCCESS, CANCELLED -> scheduledTasks.remove(taskID);
			case RUNNING -> LOGGER.debug("Task {} is already in progress!", taskID);
			default -> LOGGER.debug("Task {} is already scheduled!", taskID);
		}
	}

	private Runnable wrapTaskRunnable(Runnable runnable, UUID taskID) {
		return () -> {
			try {
				LOGGER.debug("Running task for {}", taskID);
				runnable.run();
			}
			catch (Exception e) {
				LOGGER.error("Failed to run scheduled task", e);
			}
			finally {
				LOGGER.debug("Task for match {} ended", taskID);
				scheduledTasks.remove(taskID);
			}
		};
	}

}
