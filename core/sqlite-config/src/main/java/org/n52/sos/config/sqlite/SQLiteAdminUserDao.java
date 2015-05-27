package org.n52.sos.config.sqlite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.config.AdminUserDao;
import org.n52.iceland.config.AdministratorUser;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.sos.config.sqlite.SQLiteSessionManager.HibernateAction;
import org.n52.sos.config.sqlite.SQLiteSessionManager.VoidHibernateAction;
import org.n52.sos.config.sqlite.entities.AdminUser;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SQLiteAdminUserDao extends AbstractSQLiteDao
        implements AdminUserDao {

    private static final Logger LOG = LoggerFactory
            .getLogger(SQLiteAdminUserDao.class);

    @Override
    public AdministratorUser getAdminUser(String username)
            throws ConnectionProviderException {
        return execute(new GetAdminUserAction(username));
    }

    @Override
    public AdministratorUser createAdminUser(String username, String password)
            throws ConnectionProviderException {
        return execute(new CreateAdminUserAction(username, password));
    }

    @Override
    public void saveAdminUser(AdministratorUser user)
            throws ConnectionProviderException {
        execute(new SaveAdminUserAction(user));
    }

    @Override
    public void deleteAdminUser(String username)
            throws ConnectionProviderException {
        execute(new DeleteAdminUserAction(username));
    }

    @Override
    public Set<AdministratorUser> getAdminUsers()
            throws ConnectionProviderException {
        return execute(new GetAdminUsersAction());
    }

    @Override
    public void deleteAll()
            throws ConnectionProviderException {
        execute(new DeleteAllAction());
    }

    private class DeleteAllAction extends VoidHibernateAction {
        @Override
        @SuppressWarnings("unchecked")
        protected void run(Session session) {
            List<AdministratorUser> users = session
                    .createCriteria(AdministratorUser.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            for (AdministratorUser u : users) {
                session.delete(u);
            }
        }
    }

    private class GetAdminUserAction implements HibernateAction<AdminUser> {
        private final String username;

        GetAdminUserAction(String username) {
            this.username = username;
        }

        @Override
        public AdminUser call(Session session) {
            return (AdminUser) session.createCriteria(AdminUser.class)
                    .add(Restrictions.eq(AdminUser.USERNAME_PROPERTY, username))
                    .uniqueResult();
        }
    }

    private class DeleteAdminUserAction extends VoidHibernateAction {
        private final String username;

        DeleteAdminUserAction(String username) {
            this.username = username;
        }

        @Override
        protected void run(Session session) {
            AdministratorUser au = (AdministratorUser) session
                    .createCriteria(AdministratorUser.class)
                    .add(Restrictions.eq(AdminUser.USERNAME_PROPERTY, username))
                    .uniqueResult();
            if (au != null) {
                session.delete(au);
            }
        }
    }

    private class GetAdminUsersAction implements
            HibernateAction<Set<AdministratorUser>> {
        @Override
        @SuppressWarnings("unchecked")
        public Set<AdministratorUser> call(Session session) {
            return new HashSet<>(session.createCriteria(AdministratorUser.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());
        }
    }

    private class SaveAdminUserAction extends VoidHibernateAction {
        private final AdministratorUser user;

        SaveAdminUserAction(AdministratorUser user) {
            this.user = user;
        }

        @Override
        protected void run(Session session) {
            LOG.debug("Updating AdministratorUser {}", user);
            session.update(user);
        }
    }

    private class CreateAdminUserAction implements HibernateAction<AdminUser> {
        private final String username;

        private final String password;

        CreateAdminUserAction(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public AdminUser call(Session session) {
            AdminUser user = new AdminUser().setUsername(username)
                    .setPassword(password);
            LOG.debug("Creating AdministratorUser {}", user);
            session.save(user);
            return user;
        }
    }
}
