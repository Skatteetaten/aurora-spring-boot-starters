# Aurora Spring Boot Starters

Starters for creating a spring boot application that integrates into the Aurora Openshift platform.

For an example on how to use these look at [the reference application](https://github.com/Skatteetaten/openshift-reference-springboot-server)

Starters included here are
- aurora: Creates property sources for Openshift properties, expose prometheus metrics
- aurora-oracle: Creates datasource for database, includes flyway. If you want another jdbc driver just exclude oracle and include yours
- aurora-spock: Dependencies for working with spock testing
