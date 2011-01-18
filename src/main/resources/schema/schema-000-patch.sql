
DROP TABLE IF EXISTS `tPatch`;

CREATE TABLE `tPatch` (
	  id		SERIAL
	, patchName	VARCHAR(150) NOT NULL
	, stamp		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

	# Ensure patches are unique entries
	, CONSTRAINT UNIQUE KEY ( patchName )
);

DROP TABLE IF EXISTS `tApplicationState`;

CREATE TABLE `tApplicationState` (
	  fKey		VARCHAR(150) NOT NULL
	, fValue	TEXT

	# Ensure patches are unique entries
	, CONSTRAINT UNIQUE KEY ( fKey )
);

INSERT INTO tApplicationState VALUES ( 'initialDataLoad', '0' );

