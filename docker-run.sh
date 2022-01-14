docker stop lagbot
docker rm lagbot
docker run -d --name lagbot --restart always lagbot
