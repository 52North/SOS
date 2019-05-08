<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
    Software GmbH

    This program is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License version 2 as published
    by the Free Software Foundation.

    If the program is linked with libraries which are licensed under one of
    the following licenses, the combination of the program with the linked
    library is not considered a "derivative work" of the program:

        - Apache License, version 2.0
        - Apache Software License, version 1.0
        - GNU Lesser General Public License, version 3
        - Mozilla Public License, versions 1.0, 1.1 and 2.0
        - Common Development and Distribution License (CDDL), version 1.0

    Therefore the distribution of the program linked with libraries licensed
    under the aforementioned licenses, is permitted by the copyright holders
    if the distribution is compliant with both the GNU General Public
    License version 2 and the aforementioned licenses.

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
    Public License for more details.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sml="http://www.opengis.net/sensorml/2.0"
                xmlns:gmd="http://www.isotc211.org/2005/gmd"
                version="2.0">
    <xsl:output method="html"/>
    <xsl:template match="/">
        <div>
            <h4>
                Keywords
            </h4>
            <table class="table table-hover">
                <xsl:for-each select="sml:PhysicalComponent/sml:keywords/sml:KeywordList/sml:keyword">
                <tr>
                    <td>
                        <xsl:value-of select="."/>
                    </td>
                </tr>
                </xsl:for-each>
            </table>
        </div>
        <div>
            <h4>
                Identifier
            </h4>
            <table class="table table-hover">
                <xsl:for-each select="sml:PhysicalComponent/sml:identification/sml:IdentifierList/sml:identifier">
                    <tr>
                        <td>
                            <xsl:value-of select="sml:Term/sml:label"/>
                        </td>
                        <td>
                            <xsl:value-of select="sml:Term/sml:value"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </div>
        <div>
            <h4>
                Contact
            </h4>
            <table class="table table-hover">
                <xsl:for-each select="sml:PhysicalComponent/sml:contacts/sml:ContactList/sml:contact">
                    <tr>
                        <td>
                            <xsl:value-of select="gmd:CI_ResponsibleParty/gmd:role"/>
                        </td>
                        <td>
                            <xsl:value-of select="gmd:CI_ResponsibleParty/gmd:organisationName"/>
                            <xsl:if test="gmd:CI_ResponsibleParty/gmd:individualName">
                                (<xsl:value-of select="gmd:CI_ResponsibleParty/gmd:individualName"/>)
                            </xsl:if>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </div>
        <div>
            <h4>
                Description
            </h4>
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:value-of select="sml:PhysicalComponent/sml:documentation/sml:DocumentList/sml:document/gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
                </xsl:attribute>
                <xsl:value-of select="sml:PhysicalComponent/sml:documentation/sml:DocumentList/sml:document/gmd:CI_OnlineResource/gmd:description"/>
            </xsl:element>
        </div>
    </xsl:template>
</xsl:stylesheet>