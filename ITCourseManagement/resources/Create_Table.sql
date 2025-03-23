USE it_course_management;
CREATE TABLE ROLES(
	ROLEID INT PRIMARY KEY AUTO_INCREMENT,
    ROLENAME NVARCHAR(30)
);
CREATE TABLE USERS(
    USERID INT PRIMARY KEY AUTO_INCREMENT,
    ROLEID INT NOT NULL,
    USERFULLNAME NVARCHAR(100) NOT NULL,
    USERNAME NVARCHAR(100) NOT NULL,
    PASSWORD CHAR(20) NOT NULL,
    PHONENUMBER VARCHAR(15) ,
    EMAIL CHAR(50) NOT NULL,
    DESCRIPTION NVARCHAR(512),
    STATUS ENUM('online', 'offline', 'banned'),
    CREATEDAT DATE NOT NULL
);


CREATE TABLE COURSES(
	COURSEID INT PRIMARY KEY AUTO_INCREMENT,
    COURSENAME NVARCHAR(512) NOT NULL,
    LANGUAGE ENUM(
        'English', 'Vietnamese', 'Spanish', 'French', 'German', 'Chinese', 'Japanese', 'Korean',
        'Italian', 'Portuguese', 'Russian', 'Arabic', 'Hindi', 'Bengali', 'Urdu', 'Turkish',
        'Dutch', 'Greek', 'Polish', 'Swedish', 'Norwegian', 'Danish', 'Finnish', 'Czech',
        'Hungarian', 'Hebrew', 'Thai', 'Malay', 'Indonesian', 'Filipino', 'Ukrainian', 'Slovak',
        'Romanian', 'Bulgarian', 'Serbian', 'Croatian', 'Slovenian', 'Lithuanian', 'Latvian',
        'Estonian', 'Persian', 'Swahili', 'Zulu', 'Amharic', 'Punjabi', 'Sinhala', 'Nepali',
        'Burmese', 'Mongolian', 'Khmer'
    ) NOT NULL,
    PROGRAMMINGLANGUAGE ENUM(
        'Java', 'C', 'Cpp', 'CSharp', 'Python', 'JavaScript', 'TypeScript',
		'GoLang', 'Rust', 'Kotlin', 'Swift', 'Ruby', 'PHP', 'Perl',
		'Scala', 'Objective_C', 'Haskell', 'Lua', 'RLang', 'Dart',
		'Shell', 'SQL', 'MATLAB', 'Assembly', 'Groovy', 'FSharp',
		'Elixir', 'Erlang', 'Fortran', 'COBOL', 'VB_NET',
		'Pascal', 'Prolog', 'Scheme', 'Lisp', 'Julia', 'Solidity',
		'VHDL', 'Ada', 'Tcl', 'Crystal', 'OCaml', 'ABAP',
		'SAS', 'Hack', 'Nim', 'Delphi', 'PL_SQL', 'Bash', 'VietnamesePseudoCode'

    ) NOT NULL,
    LEVEL ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALLLEVEL') NOT NULL,
    USERID INT NOT NULL,
    FOREIGN KEY(USERID) REFERENCES USERS(USERID),
    THUMBNAILURL VARCHAR(255),
    PRICE FLOAT,
	COURSEDESCRIPTION VARCHAR(512),
    CREATEDAT DATETIME NOT NULL,
    UPDATEDAT DATETIME,
    CHECK(PRICE >= 0)
);



select * from users