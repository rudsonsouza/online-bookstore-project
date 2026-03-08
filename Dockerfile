FROM jboss/wildfly:26.1.3.Final

# Set environment variables
ENV WILDFLY_HOME /opt/jboss/wildfly
ENV DEPLOYMENT_DIR ${WILDFLY_HOME}/standalone/deployments

# Copy PostgreSQL JDBC driver module
COPY docker/wildfly/modules/org/postgresql/main/module.xml ${WILDFLY_HOME}/modules/org/postgresql/main/module.xml
COPY docker/wildfly/postgresql-42.6.0.jar ${WILDFLY_HOME}/modules/org/postgresql/main/postgresql-42.6.0.jar

# Copy WildFly CLI configuration script
COPY docker/wildfly/configure-wildfly.cli /tmp/configure-wildfly.cli

# Run the CLI script to configure datasource, JMS queues, etc.
RUN ${WILDFLY_HOME}/bin/jboss-cli.sh --echo-command \
    --file=/tmp/configure-wildfly.cli

# Copy Liquibase changelog files
COPY src/main/resources/db /opt/jboss/liquibase/db

# Copy the WAR file
COPY target/online-bookstore.war ${DEPLOYMENT_DIR}/

# Expose ports
EXPOSE 8080 9990

# Start WildFly with full profile (includes JMS support)
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-full.xml", "-b", "0.0.0.0"]
