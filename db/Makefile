
all: compile

clean:
	@echo -n "Cleaning old files ... "
	@rm -rf data schema master.db master.db.7z
	@echo "[done]"

compile: clean schema data

data: master.db
	@echo -n "Dumping data ... "
	@mkdir -p data
	@for T in `echo ".tables" | sqlite3 master.db`; do \
		echo -n "$$T "; \
		echo "SELECT * FROM $$T;" | sqlite3 master.db > data/$$T.psv; \
	done
	@echo "[done]"

master.db:
	@echo "Grabbing a copy of master.db from github ... "
	@wget -c https://github.com/downloads/freemed/openreact/master.db.7z
	@echo "Extracting master.db ... "
	@7z x master.db.7z

schema: master.db
	@echo -n "Generating schema ... "
	@mkdir -p schema
	@echo ".schema" | sqlite3 master.db > schema/000-schema.sql
	@echo "[done]"

