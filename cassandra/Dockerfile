#
# NOTE: THIS DOCKERFILE IS GENERATED VIA "apply-templates.sh"
#
# PLEASE DO NOT EDIT IT DIRECTLY.
#
# NOTE: will also work with other cassandra version tags
FROM cassandra:5.0

# Fix UTF-8 accents in init scripts
ENV LANG C.UTF-8


RUN apt-get update && apt-get install -y --no-install-recommends curl gnupg locales git apt-utils && \
  apt-get -y install --no-install-recommends python3 python3-dev python3.10-venv build-essential && \
  locale-gen en_US.UTF-8  && \
  rm -rf /var/lib/apt/lists/*

# use venv and copy to runtime
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

COPY requirements.txt /src/requirements.txt
RUN python3 -m pip install --upgrade pip && \
    pip install -r /src/requirements.txt && \
    rm -rf /root/.cache/pip

# Here, you can add any *.sh or *.cql scripts inside /docker-entrypoint-initdb.d
#  *.sh files will be executed BEFORE launching cassandra
#  *.cql files will be executed with cqlsh -f AFTER cassandra started
# Files are executed in name order (ls * | sort)
COPY *.cql /docker-entrypoint-initdb.d/

# this is the script that will patch the already existing entrypoint from cassandra image
COPY docker-entrypoint.sh /

# Override ENTRYPOINT, keep CMD
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["cassandra", "-f", "-R"]

