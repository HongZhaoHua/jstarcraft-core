package com.jstarcraft.core.storage.hibernate;

import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 驼峰转蛇形策略
 * 
 * @author Birdy
 *
 */
public class Camel2SnakeStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    private Identifier apply(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(name.getText().replace(StringUtility.PERIOD, StringUtility.UNDERSCORE));
        for (int index = 1; index < builder.length() - 1; index++) {
            if (isUnderscoreRequired(builder.charAt(index - 1), builder.charAt(index), builder.charAt(index + 1))) {
                builder.insert(index++, StringUtility.UNDERSCORE);
            }
        }
        return getIdentifier(builder.toString(), name.isQuoted(), jdbcEnvironment);
    }

    private Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        if (isCaseInsensitive(jdbcEnvironment)) {
            name = name.toLowerCase(Locale.ROOT);
        }
        return new Identifier(name, quoted);
    }

    /**
     * 指定数据库是否区分大小写
     * 
     * @param jdbcEnvironment
     * @return
     */
    private boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
        return true;
    }

    /**
     * 是否需要下划线
     * 
     * @param before
     * @param current
     * @param after
     * @return
     */
    private boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }

}