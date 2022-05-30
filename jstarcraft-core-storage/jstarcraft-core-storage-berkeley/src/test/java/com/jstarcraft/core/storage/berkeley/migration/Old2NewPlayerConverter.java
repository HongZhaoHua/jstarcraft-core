package com.jstarcraft.core.storage.berkeley.migration;

import com.jstarcraft.core.storage.berkeley.migration.BerkeleyConverter;
import com.jstarcraft.core.storage.berkeley.migration.MigrationContext;
import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;
import com.sleepycat.persist.raw.RawType;

/**
 * Old2NewPlayerConverter扩展了DefaultConverter,实现对Player实体的迁移
 * 
 * @author Birdy
 *
 */
public class Old2NewPlayerConverter extends BerkeleyConverter {

    public String SEX_FIELD = "sex";

    protected Object handleField(String fieldName, RawType fieldType, Object fieldValue, RawObject oldRawObject, RawObject newRawObject, MigrationContext context, RawStore oldRawStore, RawStore newRawStore) throws Exception {
        if (fieldName.equals(SEX_FIELD)) {
            final Object sexFieldValue = oldRawObject.getValues().get(fieldName);
            if (Boolean.class.cast(sexFieldValue)) {
                return "Boy";
            } else {
                return "Girl";
            }
        } else {
            return super.handleField(fieldName, fieldType, fieldValue, oldRawObject, newRawObject, context, oldRawStore, newRawStore);
        }
    }

}
