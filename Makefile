deps:
	docker-compose up -d

stop:
	docker-compose -f docker-compose.yml stop -t 0
	docker-compose -f docker-compose.yml rm -fv