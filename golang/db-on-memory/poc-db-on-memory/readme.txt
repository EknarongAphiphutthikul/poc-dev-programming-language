# Build image
docker build -t poc-db-on-mem:1.0 .

# Run the container
docker run -d -v <path_in_machine>:/data/app/ --name poc-db-on-mem poc-db-on-mem:1.0
docker run -it --rm -v <path_in_machine>:/data/app/ --name poc-db-on-mem poc-db-on-mem:1.0 sh
