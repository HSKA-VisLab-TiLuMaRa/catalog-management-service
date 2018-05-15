FROM java:8
ADD /target/*.jar catalog-manager.jar
ENTRYPOINT ["java","-jar","catalog-manager.jar"]