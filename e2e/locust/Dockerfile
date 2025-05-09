FROM python:3.12-slim

# Update + install build tools, it's needed by some Python lib
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    make \
    libffi-dev \
    libssl-dev \
    build-essential

COPY requirments.txt requirments.txt

RUN pip3 install -r requirments.txt

COPY locustfile.py locust.conf ./

ENTRYPOINT ["locust", "--oauth2-token"]