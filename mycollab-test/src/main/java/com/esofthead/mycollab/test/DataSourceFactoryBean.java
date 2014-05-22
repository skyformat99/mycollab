/**
 * This file is part of mycollab-test.
 *
 * mycollab-test is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-test is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-test.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.test;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 * 
 */
public class DataSourceFactoryBean extends AbstractFactoryBean<DataSource> {

	private static Logger log = LoggerFactory
			.getLogger(DataSourceFactoryBean.class);

	private HikariDataSource dataSource;

	public DataSource getDataSource() {
		try {
			return (DataSource) createInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected DataSource createInstance() throws Exception {
		TestDbConfiguration dbConf = new TestDbConfiguration();
		HikariConfig config = new HikariConfig();
		config.setMaximumPoolSize(100);
		config.setDriverClassName(dbConf.getDriverClassName());
		config.setJdbcUrl(dbConf.getJdbcUrl());
		config.addDataSourceProperty("user", dbConf.getUsername());
		config.addDataSourceProperty("password", dbConf.getPassword());
		return dataSource;
	}

	@Override
	public void destroy() throws Exception {
		super.destroy();

		if (dataSource != null) {
			dataSource.close();
			log.debug("Close connection");
		}

	}

	@Override
	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

}
