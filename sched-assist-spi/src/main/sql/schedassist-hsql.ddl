create sequence ownerid_seq
		start with 1 increment by 1;
create sequence eventid_seq
		start with 1 increment by 1;
create sequence reminderid_seq
		start with 1 increment by 1;

create table owners (
	internal_id integer not null,
	external_unique_id varchar (32) not null,
	username varchar (32) not null,
	CONSTRAINT owner_id_unq UNIQUE(internal_id),
	CONSTRAINT owner_username_unq UNIQUE(username),
	CONSTRAINT owner_externalid_unq UNIQUE(external_unique_id)
);

create table schedules (
	owner_id integer not null,
	start_time timestamp not null,
	end_time timestamp not null,
	visitor_limit integer not null,
	meeting_location varchar (128),
	CONSTRAINT fk_sched_owner FOREIGN KEY (owner_id) REFERENCES owners(internal_id) ON DELETE CASCADE
);

create table preferences (
	owner_id integer not null,
	preference_key varchar (64) not null,
	preference_value varchar (512) not null,
	CONSTRAINT fk_pref_owner FOREIGN KEY (owner_id) REFERENCES owners(internal_id) ON DELETE CASCADE
);

create table owner_adhoc_authz (
	owner_username varchar (32) not null,
	relationship varchar (64) not null,
	visitor_username varchar (32) not null,
	CONSTRAINT adhoc_unique UNIQUE (owner_username, visitor_username)
);

create unique index schedules_unique_idx 
		on schedules
		(owner_id, start_time, end_time);
		
create table advisorlist (
	advisor_emplid varchar (16) not null,
	advisor_relationship varchar (64) not null,
	student_emplid varchar (16) not null,
	term_description varchar (64) not null,
	term_number varchar (8) not null,
	advisor_type varchar (64) not null,
	committee_role varchar (64)
);

create table csv_relationships (
	owner_id varchar (16) not null,
	visitor_id varchar (16) not null,
	rel_description varchar (96) not null
);

create table event_statistics (
	event_id integer not null,
	owner_id integer not null,
	visitor_id varchar(64) not null,
	event_type varchar(32) not null,
	event_timestamp timestamp not null,
	event_start timestamp not null,
	CONSTRAINT event_id_unq UNIQUE(event_id)
);

create table public_profiles (
	owner_id integer not null,
	owner_display_name varchar (64) not null,
 	profile_key varchar (8) not null,
	profile_description varchar (512) not null,
	CONSTRAINT profile_key_unq UNIQUE(profile_key),
	CONSTRAINT fk_profile_owner FOREIGN KEY (owner_id) REFERENCES owners(internal_id) ON DELETE CASCADE
);

create table profile_tags (
	profile_key varchar (8) not null,
	tag varchar (80) not null,
	tag_display varchar (80) not null,
	CONSTRAINT fk_profile_tags FOREIGN KEY (profile_key) REFERENCES public_profiles(profile_key) ON DELETE CASCADE
);

create table reflect_locks(
	owner_id integer not null,
	constraint fk_owner_lock_id foreign key(owner_id) references owners(internal_id) on DELETE CASCADE
);

create table reminders (
	reminder_id integer not null,
	owner_id integer not null,
	recipient varchar(64) not null,
	event_start timestamp not null,
	event_end timestamp not null,
	send_time timestamp not null,
	CONSTRAINT reminder_id_unq UNIQUE(reminder_id),
	CONSTRAINT fk_reminder_owner FOREIGN KEY (owner_id) REFERENCES owners(internal_id) ON DELETE CASCADE
);