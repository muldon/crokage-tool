# crokage
Crowd Knowledge Answer Generator. This project is the back-end implementation of the [original work](https://github.com/muldon/CROKAGE-replication-package). The front-end project is [here](https://github.com/muldon/crokage-tool-fe). 

# Prerequisites
1. [Docker - without sudo](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-20-04
). 
Check that docker is running and postgres container is on: 
```
$ docker ps
```
should return the list of running containers. 

2. [Docker Compose](https://docs.docker.com/compose/install/)


# Installation

1. Download the files, as in [here](https://github.com/muldon/CROKAGE-replication-package#running-the-tool-mode-1---replication-package). Extract the files to your work dir, like /home/rodrigo/tmp/crokage-replication-package. 
2. Download and edit the files postgres-docker-compose.yml and crokage-tool-be-docker-compose.yml of this repository (crokage-tool). Set your database POSTGRES_PASSWORD credential (the same for both files). On crokage-tool-be-docker-compose.yml, set your work dir (in replace of /home/rodrigo/tmp/crokage-replication-package). Don't change the other variables (including TMP_DIR). 

3. On your work dir, run postgres-docker-compose.yml file:
```
sudo docker-compose -f postgres-docker-compose.yml up -d
```

4. Download the Dump of SO [here](http://lascam.facom.ufu.br/companion/crokage/dump2018crokagereplicationpackage.backup) (Dump of June 2018). Restore it on your postgres with the name stackoverflow2018crokagetool. PgAdmin has this feature. Right click on the created database -> Restore... select the dump (dump2018crokagereplicationpackage.backup)

5. On your work dir, run crokage-tool-be-docker.yml file: 
```
sudo docker-compose -f crokage-tool-be-docker.yml up -d
```
This will take a while. You can check if both postgres_container and crokage-tool-be are running:  
```
$ docker ps
```



