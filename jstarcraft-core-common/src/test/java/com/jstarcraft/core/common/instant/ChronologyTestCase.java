package com.jstarcraft.core.common.instant;

import org.joda.time.DateTimeZone;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.chrono.LimitChronology;
import org.joda.time.chrono.StrictChronology;
import org.joda.time.chrono.ZonedChronology;
import org.junit.Assert;
import org.junit.Test;

public class ChronologyTestCase {

    @Test
    public void testToString() {
        DateTimeZone utc = DateTimeZone.UTC;
        ISOChronology isoChronology = ISOChronology.getInstance(utc);

        Assert.assertEquals("ISOChronology[UTC]", isoChronology.toString());
        Assert.assertEquals("BuddhistChronology[UTC]", BuddhistChronology.getInstance(utc).toString());
        Assert.assertEquals("CopticChronology[UTC]", CopticChronology.getInstance(utc).toString());
        Assert.assertEquals("EthiopicChronology[UTC]", EthiopicChronology.getInstance(utc).toString());
        Assert.assertEquals("GJChronology[UTC]", GJChronology.getInstance(utc).toString());
        Assert.assertEquals("GregorianChronology[UTC]", GregorianChronology.getInstance(utc).toString());
        Assert.assertEquals("JulianChronology[UTC]", JulianChronology.getInstance(utc).toString());
        Assert.assertEquals("IslamicChronology[UTC]", IslamicChronology.getInstance(utc).toString());

        Assert.assertEquals("LenientChronology[ISOChronology[UTC]]", LenientChronology.getInstance(isoChronology).toString());
        Assert.assertEquals("LimitChronology[ISOChronology[UTC], NoLimit, NoLimit]", LimitChronology.getInstance(isoChronology, null, null).toString());
        Assert.assertEquals("StrictChronology[ISOChronology[UTC]]", StrictChronology.getInstance(isoChronology).toString());
        Assert.assertEquals("ZonedChronology[ISOChronology[UTC], UTC]", ZonedChronology.getInstance(isoChronology, utc).toString());
    }

}
