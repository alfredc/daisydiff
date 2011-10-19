/*
 * Copyright 2004 Guy Van den Broeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.outerj.daisy.diff;

/* new diffHTML stuff (see Main.java) */
import java.io.StringReader;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.XslFilter;
import org.xml.sax.helpers.AttributesImpl;

/* old diffHTML stuff */
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.outerj.daisy.diff.tag.TagComparator;
import org.outerj.daisy.diff.tag.TagDiffer;
import org.outerj.daisy.diff.tag.TagSaxDiffOutput;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class DaisyDiff {

    /**
     * For embedded 3-way diffs: takes 3 strings and returns a string.
     */
    public static String diffHTML(String baseString, String leftString,
            String rightString) throws URISyntaxException {

        boolean htmlOut = true;

        StringWriter outputString = new StringWriter();

        try {
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory
                    .newInstance();

            TransformerHandler result = tf.newTransformerHandler();
            result.setResult(new StreamResult(outputString));

            StringReader baseStream, leftStream, rightStream;

            baseStream = new StringReader(baseString);
            leftStream = new StringReader(leftString);
            rightStream = new StringReader(rightString);

            XslFilter filter = new XslFilter();

            ContentHandler postProcess = htmlOut? filter.xsl(result,
                    "org/outerj/daisy/diff/htmlheader.xsl"):result;

            Locale locale = Locale.getDefault();
            String prefix = "diff";

            HtmlCleaner cleaner = new HtmlCleaner();

            InputSource baseSource = new InputSource(baseStream);
            InputSource leftSource = new InputSource(leftStream);
            InputSource rightSource = new InputSource(rightStream);

            DomTreeBuilder baseHandler = new DomTreeBuilder();
            cleaner.cleanAndParse(baseSource, baseHandler);
            //System.out.print(".");
            TextNodeComparator baseComparator = new TextNodeComparator(
                    baseHandler, locale);

            DomTreeBuilder leftHandler = new DomTreeBuilder();
            cleaner.cleanAndParse(leftSource, leftHandler);
            //System.out.print(".");
            TextNodeComparator leftComparator = new TextNodeComparator(
                    leftHandler, locale);

            DomTreeBuilder rightHandler = new DomTreeBuilder();
            cleaner.cleanAndParse(rightSource, rightHandler);
            //System.out.print(".");
            TextNodeComparator rightComparator = new TextNodeComparator(
                    rightHandler, locale);

            postProcess.startDocument();
            postProcess.startElement("", "diffreport", "diffreport",
                    new AttributesImpl());
            //doCSS(css, postProcess);
            postProcess.startElement("", "diff", "diff",
                    new AttributesImpl());
            HtmlSaxDiffOutput output = new HtmlSaxDiffOutput(postProcess,
                    prefix);

            HTMLDiffer differ = new HTMLDiffer(output);
            differ.diff(baseComparator, leftComparator, rightComparator);
            //System.out.print(".");
            postProcess.endElement("", "diff", "diff");
            postProcess.endElement("", "diffreport", "diffreport");
            postProcess.endDocument();

            return outputString.toString();
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    /**
     * Diffs two html files, outputting the result to the specified consumer.
     */
    public static void diffHTML(InputSource oldSource, InputSource newSource,
            ContentHandler consumer, String prefix, Locale locale)
            throws SAXException, IOException {

        DomTreeBuilder oldHandler = new DomTreeBuilder();
        XMLReader xr1 = XMLReaderFactory.createXMLReader();
        xr1.setContentHandler(oldHandler);
        xr1.parse(oldSource);
        TextNodeComparator leftComparator = new TextNodeComparator(oldHandler,
                locale);

        DomTreeBuilder newHandler = new DomTreeBuilder();
        XMLReader xr2 = XMLReaderFactory.createXMLReader();
        xr2.setContentHandler(newHandler);
        xr2.parse(newSource);

        TextNodeComparator rightComparator = new TextNodeComparator(newHandler,
                locale);

        HtmlSaxDiffOutput output = new HtmlSaxDiffOutput(consumer, prefix);
        HTMLDiffer differ = new HTMLDiffer(output);
        differ.diff(leftComparator, rightComparator);
    }

    /**
     * Diffs two html files word for word as source, outputting the result to
     * the specified consumer.
     */
    public static void diffTag(String oldText, String newText,
            ContentHandler consumer) throws Exception {
        consumer.startDocument();
        TagComparator oldComp = new TagComparator(oldText);
        TagComparator newComp = new TagComparator(newText);

        TagSaxDiffOutput output = new TagSaxDiffOutput(consumer);
        TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);
        consumer.endDocument();
    }

    /**
     * Diffs two html files word for word as source, outputting the result to
     * the specified consumer.
     */
    public static void diffTag(BufferedReader oldText, BufferedReader newText,
            ContentHandler consumer) throws Exception {

        TagComparator oldComp = new TagComparator(oldText);
        TagComparator newComp = new TagComparator(newText);

        TagSaxDiffOutput output = new TagSaxDiffOutput(consumer);
        TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);
    }

}
