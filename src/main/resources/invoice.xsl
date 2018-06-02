<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="utf-8" indent="yes" />

    <xsl:template match="root">
        <xsl:apply-templates select="./pb138-accexpenses/record" mode="whole">
            <xsl:with-param name="whoami" select='"expense"'/>
        </xsl:apply-templates>
        <xsl:apply-templates select="./pb138-accearnings/record" mode="whole">
            <xsl:with-param name="whoami" select='"revenue"'/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="record" mode="whole">
        <xsl:param name="whoami"/>
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html>
            <head>
                <meta charset="utf-8" />
                <title>Expediture</title>
                <style>
                    .invoice-box {
                    max-width: 800px;
                    margin: auto;
                    padding: 30px;
                    border: 1px solid #eee;
                    font-size: 16px;
                    line-height: 24px;
                    font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;
                    color: #555;
                    }
                    .invoice-box table {
                    width: 100%;
                    line-height: inherit;
                    text-align: left;
                    }
                    .invoice-box table td {
                    padding: 5px;
                    vertical-align: top;
                    }
                    .invoice-box table tr td:nth-child(2) {
                    text-align: right;
                    }
                    .invoice-box table tr.top table td {
                    padding-bottom: 20px;
                    }
                    .invoice-box table tr.top table td.title {
                    font-size: 45px;
                    line-height: 45px;
                    color: #333;
                    }
                    .invoice-box table tr.information table, .invoice-box table tr.top table, .invoice-box table {
                    table-layout: fixed;
                    }
                    .invoice-box table tr.information table td {
                    padding-bottom: 40px;
                    }
                    .invoice-box table tr.heading td {
                    background: #eee;
                    border-bottom: 1px solid #ddd;
                    font-weight: bold;
                    }
                    .invoice-box table tr.details td {
                    padding-bottom: 20px;
                    }
                    .invoice-box table tr.item td {
                    border-bottom: 1px solid #eee;
                    }
                    .invoice-box table tr.total td:nth-child(2) {
                    border-top: 2px solid #eee;
                    font-weight: bold;
                    }
                    /** RTL **/
                    .rtl {
                    direction: rtl;
                    font-family: Tahoma, 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;
                    }
                    .rtl table {
                    text-align: right;
                    }
                    .rtl table tr td:nth-child(2) {
                    text-align: left;
                    }
                    .bill-to {
                    background-color: #fcfcfc;
                    border: 1px solid #efefef;
                    padding: 5px 10px;
                    text-align: left !important;
                    }
                    .bill-to h3 {
                    padding: 0 0 10px 0;
                    margin: 0;
                    }
                </style>
            </head>
            <body>
                <div class="invoice-box">
                    <table cellpadding="0" cellspacing="0">
                        <tr class="top">
                            <td colspan="5">
                                <table>
                                    <tr>
                                        <td>
                                            <h1>Invoice</h1><br />
                                            Billing date: <xsl:value-of select="billing-date" /><br />
                                            Issuing date: <xsl:value-of select="issuing-date" />
                                        </td>
                                        <td>
                                            <div class="bill-to">
                                                <h3>Shipping address:</h3>
                                                <xsl:value-of select="recipient-address" />
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr class="information">
                            <td colspan="5">
                                <table>
                                    <tr>
                                        <td>
                                            <xsl:if test="$whoami = 'expense'">
                                                <xsl:apply-templates select="/root/pb138-accowner/record"
                                                                     mode="ownerbillto"/>
                                            </xsl:if>
                                            <xsl:if test="$whoami = 'revenue'">
                                                <xsl:apply-templates select="." mode="billto"/>
                                            </xsl:if>
                                        </td>
                                        <td>
                                            <div class="bill-to">
                                                <h3>Bill to:</h3>
                                                <xsl:if test="$whoami = 'expense'">
                                                    <xsl:apply-templates select="." mode="billto"/>
                                                </xsl:if>
                                                <xsl:if test="$whoami = 'revenue'">
                                                    <xsl:apply-templates select="/root/pb138-accowner/record"
                                                                         mode="ownerbillto"/>
                                                </xsl:if>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr class="heading">
                            <td>
                                Item
                            </td>
                            <td>
                                Description
                            </td>
                            <td>
                                Quantity
                            </td>
                            <td>
                                Unit
                            </td>
                            <td>
                                Price
                            </td>
                        </tr>

                        <xsl:apply-templates select="./itemxs/item" />

                        <tr class="total">
                            <td colspan="4"></td>
                            <td>
                                Total: $<xsl:value-of select="total-price" />
                            </td>
                        </tr>
                    </table>
                </div>
            </body>
        </html>
        <xsl:text disable-output-escaping='yes'>&lt;!-- Nobody expects the spanish inquisition! --&gt;</xsl:text>
    </xsl:template>

    <xsl:template match="item">
        <tr class="item">
            <td>
                <xsl:value-of select="name" />
            </td>
            <td>
                <xsl:value-of select="description" />
            </td>
            <td>
                <xsl:value-of select="quantity" />
            </td>
            <td>
                <xsl:value-of select="unit" />
            </td>
            <td>
                $<xsl:value-of select="price" />
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="entity-email">
        <br />
        <xsl:value-of select="." />
    </xsl:template>

    <xsl:template match="entity-telephone">
        <br />
        <xsl:value-of select="." />
    </xsl:template>

    <xsl:template match="record" mode="billto">
        <xsl:value-of select="entity-name" /><br/><br/>
        <b>Address: </b><xsl:value-of select="entity-address" /><br/>
        <b>ICO: </b><xsl:value-of select="entity-ico" /><br/>
        <b>DIC: </b><xsl:value-of select="entity-dic" /><br/><br/>
        <b>Bank information:</b><br/>
        <xsl:value-of select="entity-bank-information" /><br/><br/>
        <b>Contacts:</b>

        <xsl:apply-templates select="./entity-telephonexs/entity-telephone" />


        <xsl:apply-templates select="./entity-emailxs/entity-email" />
    </xsl:template>

    <xsl:template match="record" mode="seller">
    </xsl:template>

    <xsl:template match="record" mode="ownerbillto">
        <xsl:value-of select="name" /><br/><br/>
        <b>Address: </b><xsl:value-of select="address" /><br/>
        <b>ICO: </b><xsl:value-of select="ico" /><br/>
        <b>DIC: </b><xsl:value-of select="dic" /><br/><br/>
        <b>Bank information:</b><br/>
        <xsl:value-of select="bank-information" /><br/><br/>
        <b>Contacts:</b>

        <xsl:apply-templates select="./telephonexs/telephone" />

        <xsl:apply-templates select="./emailxs/email" />
    </xsl:template>

    <xsl:template match="telephone">
        <br />
        <xsl:value-of select="." />
    </xsl:template>

    <xsl:template match="email">
        <br />
        <xsl:value-of select="." />
    </xsl:template>

</xsl:stylesheet>