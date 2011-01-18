
all: compile

clean:
	@echo -n "Cleaning old files ... "
	@rm -rf data schema
	@echo "[done]"

compile: clean schema data

data:
	@echo -n "Dumping data ... "
	@mkdir -p data
	@for T in `echo ".tables" | sqlite3 master.db`; do \
		echo -n "$$T "; \
		echo "SELECT * FROM $$T;" | sqlite3 master.db > data/$$T.psv; \
	done
	@echo "[done]"

schema:
	@echo -n "Generating schema ... "
	@mkdir -p schema
	@echo ".schema" | sqlite3 master.db > schema/000-schema.sql
	@echo "[done]"

