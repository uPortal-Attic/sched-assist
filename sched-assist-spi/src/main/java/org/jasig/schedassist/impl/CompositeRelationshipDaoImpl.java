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

package org.jasig.schedassist.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * {@link RelationshipDao} implementation that delegates
 * calls to {@link #forOwner(IScheduleOwner)} and {@link #forVisitor(IScheduleVisitor)}
 * to a {@link List} of other {@link RelationshipDao} implementations
 * provided at instantiation.
 * 
 * The return values of each method are the unions of the results
 * from the configured implementations.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CompositeRelationshipDaoImpl.java 3106 2011-03-02 16:43:44Z npblair $
 */
@Service
@Qualifier("composite")
public class CompositeRelationshipDaoImpl implements RelationshipDao {

	protected List<RelationshipDao> components = new ArrayList<RelationshipDao>();
	
	/**
	 * @param components the components to set
	 */
	@Autowired
	public void setComponents(List<RelationshipDao> components) {
		this.components = components;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public List<Relationship> forOwner(final IScheduleOwner owner) {
		Set<Relationship> union = new LinkedHashSet<Relationship>();
		for(RelationshipDao dao : components) {
			List<Relationship> relationships = dao.forOwner(owner);
			union.addAll(relationships);
		}
		List<Relationship> results = new ArrayList<Relationship>();
		results.addAll(union);
		
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forVisitor(org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public List<Relationship> forVisitor(final IScheduleVisitor visitor) {
		Set<Relationship> union = new LinkedHashSet<Relationship>();
		for(RelationshipDao dao : components) {
			List<Relationship> relationships = dao.forVisitor(visitor);
			union.addAll(relationships);
		}
		List<Relationship> results = new ArrayList<Relationship>();
		results.addAll(union);
		
		return results;
	}

}
