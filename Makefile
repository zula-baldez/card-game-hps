include .env

tasks = $(filter-out $@,$(MAKECMDGOALS))
internalize = $(foreach wrd, $(1),$(wrd)-component)
action = COMPONENT="$@" $(MAKE) $(call internalize, $(call tasks))

VERSION = $(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)


push-component:
	docker compose build $(COMPONENT)
	docker tag $(PREFIX)-$(COMPONENT) $(REGISTRY)/$(COMPONENT):$(VERSION)
	docker push $(REGISTRY)/$(COMPONENT):$(VERSION)
	@echo Pushed image $(REGISTRY)/$(COMPONENT):$(VERSION)

	@if [ ! -z $(GITHUB_OUTPUT) ]; then\
		echo $(subst -,_,$(COMPONENT))_version=$(VERSION) >> $(GITHUB_OUTPUT);\
	fi


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

frontend:
	$(action)

avatars-service:
	$(action)

.PHONY: eureka-server gateway-server config-server auth-service personal-account room-service game-handler frontend avatars-service push deploy