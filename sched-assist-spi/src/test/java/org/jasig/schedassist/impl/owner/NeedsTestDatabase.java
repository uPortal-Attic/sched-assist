/**
 * 
 */
package org.jasig.schedassist.impl.owner;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

/**
 * Base class for Test classes that need a database initialized with tables/sequences.
 * 
 * Depends on spring configuration in database-test.xml (on classpath, src/test/resources).
 * 
 * @author Nicholas Blair
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:database-test.xml"})
public abstract class NeedsTestDatabase extends AbstractJUnit4SpringContextTests {

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void createDatabase() throws Exception {
		Resource createDdl = (Resource) this.applicationContext.getBean("createDdl");
		
		SimpleJdbcTemplate template = new SimpleJdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		SimpleJdbcTestUtils.executeSqlScript(template, createDdl, false);
		afterCreate();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@After
	public void destroyDatabase() throws Exception {
		Resource destroyDdl = (Resource) this.applicationContext.getBean("destroyDdl");
		
		String sql = IOUtils.toString(destroyDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
		
		afterDestroy();
	}
	
	/**
	 * Allows subclasses to run code after {@link #createDatabase()}.
	 */
	public abstract void afterCreate() throws Exception;
	
	/**
	 * Allows subclasses to run code after {@link #destroyDatabase()}.
	 */
	public abstract void afterDestroy() throws Exception;
}
