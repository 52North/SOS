#!make
CURL               := $(shell which curl)
DOCKER             := $(shell which docker)
BUILD_DIR          := .build
DOCKER_BUILD       := ./$(BUILD_DIR)/docker-build
DOCKER_BUILD_URL   := "https://raw.githubusercontent.com/autermann/docker-build/master/docker-build"
DOCKER_BUILD_FLAGS := --pull --latest-branch maintenance/4.4
DOCKER_BUILD_FLAGS += --maintainer "Carsten Hollmann <c.hollmann@52north.org>"
DOCKER_BUILD_FLAGS += --license "GPLv2"
DOCKER_BUILD_FLAGS += --url "https://52north.org/software/software-projects/sos/"
DOCKER_BUILD_FLAGS += --context .. --file docker/Dockerfile

.PHONY: clean docker

default: docker

$(DOCKER_BUILD):
	@mkdir -p $(BUILD_DIR)
	@$(CURL) -sLf $(DOCKER_BUILD_URL) -o $(DOCKER_BUILD)
	@chmod +x $(DOCKER_BUILD)

clean:
	@rm -rf $(BUILD_DIR)

docker: $(DOCKER_BUILD)
	$(DOCKER_BUILD) $(DOCKER_BUILD_FLAGS) .