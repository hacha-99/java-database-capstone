access cli:
    mysql: docker exec -it mysql-java-capstone mysql -u root -p cms
    <!-- mysql-java-capstone is container-name -->
    <!-- root is username -->
    <!-- cms is name of database, optional -->
    mongo: docker exec -it mongo-java-capstone mongosh -u root -p root --authenticationDatabase admin
    <!-- --authenticationDatabase admin is db, where specified user exists -->
