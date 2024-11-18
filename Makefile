include .env

tasks = $(filter-out $@,$(MAKECMDGOALS))
internalize = $(foreach wrd, $(1),$(wrd)-component)
action = COMPONENT="$@" $(MAKE) $(call internalize, $(call tasks))

push-component:
	docker-compose build $(COMPONENT)
	docker tag $(COMPONENT) $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)
	docker push $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)
	@echo Pushed image $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)

deploy:
	./envsub .env deploy.docker-compose.yml > deploy.docker-compose.temp.yml
	yc compute instance update-container $(INSTANCE) --docker-compose-file=deploy.docker-compose.temp.yml
	rm deploy.docker-compose.temp.yml

%-component:
	@echo
	@echo Unknown command $@!
	@echo
	@exit 1

eureka-server:
	$(action)

gateway-server:
	$(action)

config-server:
	$(action)

auth-service:
	$(action)

personal-account:
	$(action)

room-service:
	$(action)

game-handler:
	$(action)

.PHONY: eureka-server gateway-server config-server auth-service personal-account room-service game-handler