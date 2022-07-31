package com.jstarcraft.core.storage.berkeley;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Berkeley版本
 * 
 * @author Birdy
 *
 */
class BerkeleyVersion {

    private final BerkeleyIdentification identification;

    private final Object instance;

    private final BerkeleyMetadata metadata;

    private int version;

    BerkeleyVersion(BerkeleyMetadata metadata, Object instance) {
        this.identification = new BerkeleyIdentification(instance.getClass(), metadata.getPrimaryValue(instance));
        this.instance = instance;
        this.metadata = metadata;
        this.version = metadata.getVersionValue(instance);
    }

    public BerkeleyIdentification getIdentification() {
        return identification;
    }

    public synchronized int getVersion() {
        return version;
    }

    synchronized int modify() {
        version++;
        metadata.setVersionValue(instance, version);
        return version;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        BerkeleyVersion that = (BerkeleyVersion) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.identification, that.identification);
        equal.append(this.version, that.version);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(identification);
        hash.append(version);
        return hash.toHashCode();
    }

}
