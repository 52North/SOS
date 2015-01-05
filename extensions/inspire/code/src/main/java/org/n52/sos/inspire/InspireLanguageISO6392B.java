/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.inspire;

import java.util.Locale;

/**
 * Enum for the ISO6392B three character languages
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public enum InspireLanguageISO6392B {
    AAR("aar"), 
    ABK("abk"), 
    ACE("ace"), 
    ACH("ach"), 
    ADA("ada"), 
    ADY("ady"), 
    AFA("afa"), 
    AFH("afh"), 
    AFR("afr"), 
    AIN("ain"), 
    AKA("aka"), 
    AKK("akk"), 
    ALB("alb"), 
    ALE("ale"), 
    ALG("alg"), 
    ALT("alt"), 
    AMH("amh"), 
    ANG("ang"), 
    ANP("anp"), 
    APA("apa"), 
    ARA("ara"), 
    ARC("arc"), 
    ARG("arg"), 
    ARM("arm"), 
    ARN("arn"), 
    ARP("arp"), 
    ART("art"), 
    ARW("arw"), 
    ASM("asm"), 
    AST("ast"), 
    ATH("ath"), 
    AUS("aus"), 
    AVA("ava"), 
    AVE("ave"), 
    AWA("awa"), 
    AYM("aym"), 
    AZE("aze"), 
    BAD("bad"), 
    BAI("bai"), 
    BAK("bak"), 
    BAL("bal"), 
    BAM("bam"), 
    BAN("ban"), 
    BAQ("baq"), 
    BAS("bas"), 
    BAT("bat"), 
    BEJ("bej"), 
    BEL("bel"), 
    BEM("bem"), 
    BEN("ben"), 
    BER("ber"), 
    BHO("bho"), 
    BIH("bih"), 
    BIK("bik"), 
    BIN("bin"), 
    BIS("bis"), 
    BLA("bla"), 
    BNT("bnt"), 
    BOS("bos"), 
    BRA("bra"), 
    BRE("bre"), 
    BTK("btk"), 
    BUA("bua"), 
    BUG("bug"), 
    BUL("bul"), 
    BUR("bur"), 
    BYN("byn"), 
    CAD("cad"), 
    CAI("cai"), 
    CAR("car"), 
    CAT("cat"), 
    CAU("cau"), 
    CEB("ceb"), 
    CEL("cel"), 
    CHA("cha"), 
    CHB("chb"), 
    CHE("che"), 
    CHG("chg"), 
    CHI("chi"), 
    CHK("chk"), 
    CHM("chm"), 
    CHN("chn"), 
    CHO("cho"), 
    CHP("chp"), 
    CHR("chr"), 
    CHU("chu"), 
    CHV("chv"), 
    CHY("chy"), 
    CMC("cmc"), 
    COP("cop"), 
    COR("cor"), 
    COS("cos"), 
    CPE("cpe"), 
    CPF("cpf"), 
    CPP("cpp"), 
    CRE("cre"), 
    CRH("crh"), 
    CRP("crp"), 
    CSB("csb"), 
    CUS("cus"), 
    CZE("cze"), 
    DAK("dak"), 
    DAN("dan"), 
    DAR("dar"), 
    DAY("day"), 
    DEL("del"), 
    DEN("den"), 
    DGR("dgr"), 
    DIN("din"), 
    DIV("div"), 
    DOI("doi"), 
    DRA("dra"), 
    DSB("dsb"), 
    DUA("dua"), 
    DUM("dum"), 
    DUT("dut"), 
    DYU("dyu"), 
    DZO("dzo"), 
    EFI("efi"), 
    EGY("egy"), 
    EKA("eka"), 
    ELX("elx"), 
    ENG("eng"), 
    ENM("enm"), 
    EPO("epo"), 
    EST("est"), 
    EWW("ewe"), 
    EWO("ewo"), 
    FAN("fan"), 
    FAO("fao"), 
    FAT("fat"), 
    FIJ("fij"), 
    FIL("fil"), 
    FIN("fin"), 
    FIU("fiu"), 
    FON("fon"), 
    FRE("fre"), 
    FRM("frm"), 
    FRO("fro"), 
    FRR("frr"), 
    FRS("frs"), 
    FRY("fry"), 
    FUL("ful"), 
    FUR("fur"), 
    GAA("gaa"), 
    GAY("gay"), 
    GBA("gba"), 
    GEM("gem"), 
    GEO("geo"), 
    GER("ger"), 
    GEZ("gez"), 
    GIL("gil"), 
    GLA("gla"), 
    GLE("gle"), 
    GLG("glg"), 
    GLV("glv"), 
    GMH("gmh"), 
    GOH("goh"), 
    GON("gon"), 
    GOR("gor"), 
    GOT("got"), 
    GRB("grb"), 
    GRC("grc"), 
    GRE("gre"), 
    GRN("grn"), 
    GSW("gsw"), 
    GUJ("guj"), 
    GWI("gwi"), 
    HAI("hai"), 
    HAT("hat"), 
    HAU("hau"), 
    HAW("haw"), 
    HEB("heb"), 
    HER("her"), 
    HIL("hil"), 
    HIM("him"), 
    HIN("hin"), 
    HIT("hit"), 
    HMN("hmn"), 
    HMO("hmo"), 
    HRV("hrv"), 
    HSB("hsb"), 
    HUN("hun"), 
    HUP("hup"), 
    IBA("iba"), 
    IBO("ibo"), 
    ICE("ice"), 
    IDO("ido"), 
    III("iii"), 
    IJO("ijo"), 
    IKU("iku"), 
    ILE("ile"), 
    ILO("ilo"), 
    INA("ina"), 
    INC("inc"), 
    IND("ind"), 
    INE("ine"), 
    INH("inh"), 
    IPK("ipk"), 
    IRA("ira"), 
    IRO("iro"), 
    ITA("ita"), 
    JAV("jav"), 
    JBO("jbo"), 
    JPN("jpn"), 
    JPR("jpr"), 
    JRB("jrb"), 
    KAA("kaa"), 
    KAB("kab"), 
    KAC("kac"), 
    KAL("kal"), 
    KAM("kam"), 
    KAN("kan"), 
    KAR("kar"), 
    KAS("kas"), 
    KAU("kau"), 
    KAW("kaw"), 
    KAZ("kaz"), 
    KBD("kbd"), 
    KHA("kha"), 
    KHI("khi"), 
    KHM("khm"), 
    KHO("kho"), 
    KIK("kik"), 
    KIN("kin"), 
    KIR("kir"), 
    KMB("kmb"), 
    KOK("kok"), 
    KOM("kom"), 
    KON("kon"), 
    KOR("kor"), 
    KOS("kos"), 
    KPE("kpe"), 
    KRC("krc"), 
    KRL("krl"), 
    KRO("kro"), 
    KRU("kru"), 
    KUA("kua"), 
    KUM("kum"), 
    KUR("kur"), 
    KUT("kut"), 
    LAD("lad"), 
    LAH("lah"), 
    LAM("lam"), 
    LAO("lao"), 
    LAT("lat"), 
    LAV("lav"), 
    LEZ("lez"), 
    LIM("lim"), 
    LIN("lin"), 
    LIT("lit"), 
    LOL("lol"), 
    LOZ("loz"), 
    LTZ("ltz"), 
    LUA("lua"), 
    LUB("lub"), 
    LUG("lug"), 
    LUI("lui"), 
    LUN("lun"), 
    LUO("luo"), 
    LOS("lus"), 
    MAC("mac"), 
    MAD("mad"), 
    MAG("mag"), 
    MAH("mah"), 
    MAI("mai"), 
    MAK("mak"), 
    MAL("mal"), 
    MAN("man"), 
    MAO("mao"), 
    MAP("map"), 
    MAR("mar"), 
    MAS("mas"), 
    MAY("may"), 
    MDF("mdf"), 
    MDR("mdr"), 
    MEN("men"), 
    MGA("mga"), 
    MIC("mic"), 
    MIN("min"), 
    MIS("mis"), 
    MKH("mkh"), 
    MLG("mlg"), 
    MLT("mlt"), 
    MNC("mnc"), 
    MNI("mni"), 
    MNO("mno"), 
    MOH("moh"), 
    MON("mon"), 
    MOS("mos"), 
    MUL("mul"), 
    MUN("mun"), 
    MUS("mus"), 
    MWL("mwl"), 
    MWR("mwr"), 
    MYN("myn"), 
    MYV("myv"), 
    NAH("nah"), 
    NAI("nai"), 
    NAP("nap"), 
    NAU("nau"), 
    NAV("nav"), 
    NBL("nbl"), 
    NDE("nde"), 
    NDO("ndo"), 
    NDS("nds"), 
    NEP("nep"), 
    NEW("new"), 
    NIA("nia"), 
    NIC("nic"), 
    NIU("niu"), 
    NNO("nno"), 
    NOB("nob"), 
    NOG("nog"), 
    NON("non"), 
    NOR("nor"), 
    NQO("nqo"), 
    NSO("nso"), 
    NUB("nub"), 
    NXC("nwc"), 
    NYA("nya"), 
    NYM("nym"), 
    NYN("nyn"), 
    NYO("nyo"), 
    NZI("nzi"), 
    OCI("oci"), 
    OJI("oji"), 
    ORI("ori"), 
    ORM("orm"), 
    OSA("osa"), 
    OSS("oss"), 
    OTA("ota"), 
    OTO("oto"), 
    PAA("paa"), 
    PAG("pag"), 
    PAL("pal"), 
    PAM("pam"), 
    PAN("pan"), 
    PAP("pap"), 
    PAU("pau"), 
    PEO("peo"), 
    PER("per"), 
    PHI("phi"), 
    PHN("phn"), 
    PLI("pli"), 
    POL("pol"), 
    PON("pon"), 
    POR("por"), 
    PRA("pra"), 
    PRO("pro"), 
    PUS("pus"), 
    QAA_QTZ("qaa-qtz"), 
    QUE("que"), 
    RAJ("raj"), 
    RAP("rap"), 
    RAR("rar"), 
    ROA("roa"), 
    ROH("roh"), 
    ROM("rom"), 
    RUM("rum"), 
    RUN("run"), 
    RUP("rup"), 
    RUS("rus"), 
    SAD("sad"), 
    SAG("sag"), 
    SAH("sah"), 
    SAI("sai"), 
    SAL("sal"), 
    SAM("sam"), 
    SAN("san"), 
    SAS("sas"), 
    SAT("sat"), 
    SCN("scn"), 
    SCO("sco"), 
    SEL("sel"), 
    SEM("sem"), 
    SGA("sga"), 
    SGN("sgn"), 
    SHN("shn"), 
    SID("sid"), 
    SIN("sin"), 
    SIO("sio"), 
    SIT("sit"), 
    SLA("sla"), 
    SLO("slo"), 
    SLV("slv"), 
    SMA("sma"), 
    SME("sme"), 
    SMI("smi"), 
    SMJ("smj"), 
    SMN("smn"), 
    SMO("smo"), 
    SMS("sms"), 
    SNA("sna"), 
    SND("snd"), 
    SNK("snk"), 
    SOG("sog"), 
    SOM("som"), 
    SON("son"), 
    SOT("sot"), 
    SPA("spa"), 
    SRD("srd"), 
    SRN("srn"), 
    SRP("srp"), 
    SRR("srr"), 
    SSA("ssa"), 
    SSW("ssw"), 
    SUK("suk"), 
    SUN("sun"), 
    SUA("sus"), 
    SUX("sux"), 
    SWA("swa"), 
    SWE("swe"), 
    SYC("syc"), 
    SYR("syr"), 
    TAH("tah"), 
    TAI("tai"), 
    TAM("tam"), 
    TAT("tat"), 
    TEL("tel"), 
    TEM("tem"), 
    TER("ter"), 
    TET("tet"), 
    TGK("tgk"), 
    TGL("tgl"), 
    THA("tha"), 
    TIB("tib"), 
    TIG("tig"), 
    TIR("tir"), 
    TIV("tiv"), 
    TKL("tkl"), 
    TLH("tlh"), 
    TLI("tli"), 
    TMH("tmh"), 
    TOG("tog"), 
    TON("ton"), 
    TPI("tpi"), 
    TSI("tsi"), 
    TSN("tsn"), 
    TSO("tso"), 
    TUK("tuk"), 
    TUM("tum"), 
    TUP("tup"), 
    TUR("tur"), 
    TUT("tut"), 
    TVL("tvl"), 
    TWI("twi"), 
    TYV("tyv"), 
    UDM("udm"), 
    UGA("uga"), 
    UIG("uig"), 
    UKR("ukr"), 
    UMB("umb"), 
    UND("und"), 
    URD("urd"), 
    UZB("uzb"), 
    VAI("vai"), 
    VEN("ven"), 
    VIE("vie"), 
    VOL("vol"), 
    VOT("vot"), 
    WAK("wak"), 
    WAL("wal"), 
    WAR("war"), 
    WAS("was"), 
    WEL("wel"), 
    WEN("wen"), 
    WLN("wln"), 
    WOL("wol"), 
    XAL("xal"), 
    XHO("xho"), 
    YAO("yao"), 
    YAP("yap"), 
    YID("yid"), 
    YOR("yor"), 
    YPK("ypk"), 
    ZAP("zap"), 
    ZBL("zbl"), 
    ZEN("zen"), 
    ZHA("zha"), 
    ZND("znd"), 
    ZUL("zul"), 
    ZUN("zun"), 
    ZXX("zxx"), 
    ZZA("zza");
           
    private final String value;

    /**
     * constructor
     * 
     * @param v
     *            the three character language string
     */
    InspireLanguageISO6392B(String v) {
        value = v;
    }

    /**
     * Get the value, three character language string
     * 
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Get {@link InspireLanguageISO6392B} for string value
     * 
     * @param v
     *            the string value to get {@link InspireLanguageISO6392B} for
     * @return {@link InspireLanguageISO6392B} of string value
     * @throws IllegalArgumentException
     *             if the string value is invalid
     */
    public static InspireLanguageISO6392B fromValue(String v) {
        for (InspireLanguageISO6392B c : InspireLanguageISO6392B.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * Get {@link InspireLanguageISO6392B} for {@link InspireLanguageISO6392B}
     * 
     * @param v
     *            {@link InspireEuLanguageISO6392B} to get
     *            {@link InspireLanguageISO6392B} for
     * @return {@link InspireLanguageISO6392B} of
     *         {@link InspireEuLanguageISO6392B}
     * @throws IllegalArgumentException
     *             if the {@link InspireEuLanguageISO6392B} is invalid
     */
    public static InspireLanguageISO6392B fromValue(InspireEuLanguageISO6392B v) {
        for (InspireLanguageISO6392B c : InspireLanguageISO6392B.values()) {
            if (c.value.equalsIgnoreCase(v.value())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.value());
    }

    /**
     * Get {@link InspireLanguageISO6392B} for {@link Locale}
     * 
     * @param v
     *            {@link Locale} to get {@link InspireLanguageISO6392B} for
     * @return {@link InspireLanguageISO6392B} of {@link Locale}
     * @throws IllegalArgumentException
     *             if the {@link Locale} is invalid
     */
    public static InspireLanguageISO6392B fromValue(Locale v) {
        for (InspireLanguageISO6392B c : InspireLanguageISO6392B.values()) {
            if (c.value.equalsIgnoreCase(v.getISO3Country()) || c.value.equalsIgnoreCase(v.getISO3Language())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.getISO3Country());
    }

}
