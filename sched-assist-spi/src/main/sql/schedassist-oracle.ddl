/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

create sequence ownerid_seq
		start with 1
		NOMAXVALUE
		NOCYCLE
		NOCACHE
;
create sequence eventid_seq
		start with 1
		NOMAXVALUE
		NOCYCLE
		NOCACHE
;
create sequence reminderid_seq
		start with 1
		NOMAXVALUE
		NOCYCLE
		NOCACHE
;

create table owners (
	internal_id int primary key,
	external_unique_id varchar2(32) not null,
	username varchar2(32) not null
);

create table schedules (
	owner_id int constraint fk_owner_id references owners (internal_id) ON DELETE CASCADE not null,
	start_time date not null,
	end_time date not null,
	visitor_limit int not null
);

create table preferences (
	owner_id int constraint fk_owner_pref_id references owners (internal_id) ON DELETE CASCADE not null,
	preference_key varchar2(64) not null,
	preference_value varchar2(512) not null
);

create table owner_adhoc_authz (
	owner_username varchar2(32) not null,
	relationship varchar2(64) not null,
	visitor_username varchar2(32) not null,
	constraint adhoc_unique unique (owner_username, visitor_username)
);

create unique index schedules_unique_idx 
		on schedules
		(owner_id, start_time, end_time)
		logging
		tablespace data
		noparallel
		compute statistics;
		
create table advisorlist (
	advisor_emplid varchar2(16) not null,
	advisor_relationship varchar2(64) not null,
	student_emplid varchar2(16) not null,
	term_description varchar2(64) not null,
	term_number varchar2(8) not null,
	advisor_type varchar2(64) not null,
	committee_role varchar2(64)
);

create table csv_relationships (
	owner_id varchar2(16) not null,
	visitor_id varchar2(16) not null,
	rel_description varchar2(96) not null
);

create table event_statistics (
	event_id int primary key,
	owner_id int not null,
	visitor_id varchar2(64) not null,
	event_type varchar2(32) not null,
	event_timestamp date not null,
	event_start date not null
);

create table public_profiles (
	owner_id int constraint fk_owner_profile_id references owners (internal_id) ON DELETE CASCADE not null,
	owner_display_name varchar2 (64) not null,
 	profile_key varchar2 (8) not null,
	profile_description varchar2 (512) not null,
	constraint profile_key_unq unique (profile_key)
);

create table profile_tags (
	profile_key varchar2 (8) constraint fk_profile_tag_key references public_profiles(profile_key) ON DELETE CASCADE not null,
	tag varchar2 (80) not null,
	tag_display varchar2 (80) not null
);

create table reflect_locks (
	owner_id int constraint fk_owner_lock_id references owners (internal_id) ON DELETE CASCADE not null
);

create table reminders (
	reminder_id int primary key,
	owner_id int constraint fk_owner_reminder_id references owners (internal_id) ON DELETE CASCADE not null,
	recipient varchar2 (64) not null,
	event_start date not null,
	event_end date not null,
	send_time date not null
);