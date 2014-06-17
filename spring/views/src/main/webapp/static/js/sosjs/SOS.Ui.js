/*
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
/*
* Copyright (C) 2012-2014 52�North Initiative for Geospatial Open Source
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
* - Apache License, version 2.0
* - Apache Software License, version 1.0
* - GNU Lesser General Public License, version 3
* - Mozilla Public License, versions 1.0, 1.1 and 2.0
* - Common Development and Distribution License (CDDL), version 1.0
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
/******************************************************************************
* Project: SOS
* Module:  SOS.Ui.js
* Purpose: User Interface library of the SOS project
* Author:  Paul M. Breen
* Date:    2012-12-12
* Id:      $Id$
******************************************************************************/

/**
 * SOS.Ui
 *
 * @fileOverview SOS.Ui Classes, built on the SOS Class (which in turn is
 * built on the OpenLayers SOS support).  Additionally, SOS.Plot is also
 * built on the jquery.flot plotting library
 * @name SOS.Ui
 */

/* The SOS.Ui objects are built on top of SOS, OL & jquery.flot */
if(typeof OpenLayers !== "undefined" && OpenLayers !== null &&
   typeof SOS !== "undefined" && SOS !== null &&
   typeof jQuery !== "undefined" && jQuery !== null &&
   typeof jQuery.plot !== "undefined" && jQuery.plot !== null) {
  /* Enable internationalisation of all strings */
  OpenLayers.Lang.setCode("en");
  OpenLayers.Util.extend(OpenLayers.Lang.en, {
    "SOSObservedPropertyString": "Observed Property",
    "SOSTimeString": "Time",
    "SOSValueString": "Value"
  });

  /* Create the SOS.Ui namespace */
  if(typeof SOS.Ui === "undefined") {
    /**
     * SOS.Ui Class
     * Base class for SOS User Interface objects.  This class marshalls access
     * to underlying SOS core objects, such as SOS, SOS.Offering etc.
     */
    SOS.Ui = OpenLayers.Class({
      url: null,
      sos: null,
      offering: null,
      CLASS_NAME: "SOS.Ui",

      /**
       * Constructor for a SOS.Ui object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.offering = null;
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.Ui object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set the internal SOS object
       */
      setSos: function(obj) {
        if(obj instanceof SOS) {
          this.sos = obj;
        }
      },

      /**
       * Get the internal SOS object
       */
      getSos: function() {
        if(!this.haveValidSosObject()) {
          if(SOS.Utils.isValidObject(this.url)) {
            this.sos = new SOS({url: this.url});
          }
        }

        return this.sos;
      },

      /**
       * Validate the internal SOS object
       */
      haveValidSosObject: function() {
        return SOS.Utils.isValidObject(this.sos);
      },

      /**
       * Get the internal sos.SOSCapabilities object
       */
      getCapabilities: function(callback) {
        if(!this.haveValidSosObject()) {
          this.getSos();
        }

        if(this.haveValidSosObject()) {
          if(!this.sos.haveValidCapabilitiesObject()) {
            // Optionally the caller can register a callback for caps request
            if(arguments.length > 0) {
              this.sos.registerUserCallback({event: "sosCapsAvailable", scope: this, callback: callback});
            }
            this.sos.getCapabilities();
          }
        }
      },

      /**
       * Validate the internal sos.SOSCapabilities object
       */
      haveValidCapabilitiesObject: function() {
        return (this.haveValidSosObject() && this.sos.haveValidCapabilitiesObject());
      },

      /**
       * Set the internal SOS.Offering object
       */
      setOffering: function(obj) {
        if(obj instanceof SOS.Offering) {
          this.offering = obj;
        }
      },

      /**
       * Get the internal SOS.Offering object
       */
      getOffering: function() {
        if(!this.haveValidCapabilitiesObject()) {
          this.getCapabilities(this._getOffering);
        } else {
          this._getOffering();
        }

        return this.offering;
      },

      /**
       * Store the internal SOS.Offering object from a call to
       * sos.getOffering()
       */
      _getOffering: function() {
        if(SOS.Utils.isValidObject(this.offeringId)) {
          this.offering = this.sos.getOffering(this.offeringId);
        }
      },

      /**
       * Validate the internal SOS.Offering object
       */
      haveValidOfferingObject: function() {
        return SOS.Utils.isValidObject(this.offering);
      },

      /**
       * Determine the time parameters for performing a getObservation request
       */
      determineObservationQueryTimeParameters: function() {
        if(!(SOS.Utils.isValidObject(this.startDatetime) && SOS.Utils.isValidObject(this.endDatetime))) {
          var relativeTime = this.relativeTime || "today";
          var t = SOS.Utils.parseRelativeTime(relativeTime);
          this.startDatetime = t.start.toISOString();
          this.endDatetime = t.end.toISOString();
        }
      },

      /**
       * Initialise a data table
       */
      initDataTable: function() {
        var table = {label: "", headerLabel: "", ordinateLabel: "", name: "", uom: "", uomTitle: "", foiName: "", offeringName: "", data: []};

        return table;
      },

      /**
       * Initialise the default label templates for a data table
       */
      initLabelTemplates: function() {
        var labelTemplates = {
          label: "[%foiName%] [%name%]",
          headerLabel: "[%foiName%] [%name%] / [%uomTitle%]",
          ordinateLabel: "[%name%] / [%uomTitle%]"
        };

        return labelTemplates;
      },

      /**
       * Get data from given SOS query result object & return as a table
       * suitable for displaying.  Optionally format the table labels
       * according to the given templates, otherwise defaults are used.  If an
       * optional filter is given, then only return observations that match
       * the filter rules
       */
      constructDataTable: function(res, labelTemplates, filter) {
        var labelTemplates = labelTemplates || this.initLabelTemplates();
        var table = this.initDataTable();

        // Construct the data table
        for(var i = 0, len = res.getCountOfObservations(); i < len; i++) {
          var ob = res.getFilteredObservationRecord(i, filter);

          if(ob) {
            if(table.name.length < 1) {
              table.name = ob.observedPropertyTitle;
            }
            if(table.uom.length < 1) {
              table.uom = ob.result.uom;
              table.uomTitle = ob.uomTitle;
            }
            if(table.foiName.length < 1) {
              var foi = res.getFeatureOfInterestFromObservationRecord(ob);

              if(foi) {
                table.foiName = foi.attributes.name;
              }
            }
            table.data.push([SOS.Utils.isoToJsTimestamp(ob.time), ob.result.value]);
          }
        }

        if(res.name) {
          table.offeringName = res.name;
        } else if(res.id) {
          table.offeringName = res.id;
        }

        // Construct the table labels according to the templates
        this.constructLabels(table, labelTemplates);

        return table;
      },

      /**
       * Construct table labels from the parsed metadata in the table object,
       * according to the given templates.  Given a table: {name: "a", ...},
       * & templates: {label: "[%name%] ...", headerLabel: "[%name%] ..."},
       * this will infill table thus: {label: "a ...", headerLabel: "a ..."}
       */
      constructLabels: function(table, templates) {
        if(SOS.Utils.isValidObject(table) && SOS.Utils.isValidObject(templates)) {
          table.label = SOS.Utils.applyTemplate(table, templates.label);
          table.headerLabel = SOS.Utils.applyTemplate(table, templates.headerLabel);
          table.ordinateLabel = SOS.Utils.applyTemplate(table, templates.ordinateLabel);
        }

        return table;
      },

      /**
       * Get data from given SOS query result object & return as an array of
       * tables suitable for displaying.  Format the table labels according
       * to the given templates
       */
      constructDataSeries: function(offering, labelTemplates) {
        var tables = [];
        var fois = offering.getFeatureOfInterestIds();

        /* If foiId wasn't passed in the request & the offering is a
           multi-station offering, then we filter the result on FOI (station),
           to produce a number of data tables */
        if(!SOS.Utils.isValidObject(offering.foiId) && fois.length > 1) {
          for(var i = 0, len = fois.length; i < len; i++) {
            tables.push(this.constructDataTable(offering, labelTemplates, {foiId: fois[i]}));
          }
        } else {
          tables.push(this.constructDataTable(offering, labelTemplates));
        }

        return tables;
      },

      /**
       * Subset an existing data series
       */
      subsetDataSeries: function(series, from, to) {
        var subset = [], n = 0;

        for(var i = 0, slen = series.length; i < slen; i++) {
          subset[i] = this.initDataTable();
          n = 0;

          // Copy all metadata
          for(var key in this.initDataTable()) {
            if(key != "data") {
              subset[i][key] = series[i][key];
            }
          }

          // Only select data whose datetime lie on the given closed interval
          for(var j = 0, tlen = series[i].data.length; j < tlen; j++) {
            if(series[i].data[j][0] >= from && series[i].data[j][0] <= to) {
              subset[i].data[n] = [];

              for(var k = 0, dlen = series[i].data[j].length; k < dlen; k++) {
                subset[i].data[n][k] = series[i].data[j][k];
              }
              n++;
            }
          }
        }

        return subset;
      },

      /**
       * Get a total count of all series data
       */
      getCountOfSeriesData: function(series) {
        var n = 0;

        if(series) {
          for(var i = 0, len = series.length; i < len; i++) {
            if(series[i].data) {
              n += series[i].data.length;
            }
          }
        }

        return n;
      },

      /**
       * Format the given value for display (simple)
       */
      formatValueSimple: function(v, L, N) {
        var x = parseFloat(v);
        return (Math.abs(x) < L && x != 0 ? x.toExponential(N) : x.toFixed(N));
      },

      /**
       * Format the given value for display (fancy)
       */
      formatValueFancy: function(v, L, N) {
        var x = parseFloat(v);

        if(Math.abs(x) < L && x != 0) {
          x = x.toExponential(N);
          x = x.replace(/e(.+)/, function(match, $1, offset, original) {return (" x 10 <sup>" + $1 + "</sup>");});
        } else {
          x = x.toFixed(N);
        }

        return x;
      },

      /**
       * Format the given message text for display, with optional level.  The
       * level numbers are based on syslog levels (0 = emergency, ...,
       * 7 = debug)
       */
      formatMessage: function(text, options) {
        var options = options || {level: {text: "", suffix: "", n: 6}};
        var container = jQuery('<div></div>', {
          "class": "ui-corner-all sos-message-container"
        });
        var paragraph = jQuery('<p></p>');
        var icon = jQuery('<span></span>', {
          "class": "ui-icon sos-message-icon"
        });
        var level = jQuery('<strong></strong>', {
          html: ((options.level && options.level.text ? options.level.text : "") + (options.level && options.level.suffix ? options.level.suffix : ""))
        });
        var message = jQuery('<span></span>', {
          html: text
        });

        if(options.level && options.level.n < 4) {
          container.addClass("ui-state-error");
          icon.addClass("ui-icon-alert");
        } else {
          container.addClass("ui-state-highlight");
          icon.addClass("ui-icon-info");
        }
        container.append(paragraph);
        paragraph.append(icon);
        paragraph.append(level);
        paragraph.append(message);

        return container;
      },

      /**
       * Format the given message text as an information-level message
       */
      formatInformationMessage: function(text) {
        var options = {level: {n: 6}};
        return this.formatMessage(text, options);
      },

      /**
       * Format the given message text as an alert-level message
       */
      formatAlertMessage: function(text) {
        var options = {level: {n: 1}};
        return this.formatMessage(text, options);
      },

      /**
       * Display summary stats about the given selected observation data
       */
      displaySelectedIntervalStats: function(container, selected) {
        var series = selected[0].item.series;
        var start = Math.min(selected[0].item.dataIndex, selected[1].item.dataIndex);
        var end = Math.max(selected[0].item.dataIndex, selected[1].item.dataIndex);
        var subset = series.data.slice(start, end + 1);
        var values = SOS.Utils.extractColumn(subset, 1);
        var stats = SOS.Utils.computeStats(values);
        var hist = SOS.Utils.computeHistogram(values);

        var panel = jQuery('<div/>');
        this.addSelectedIntervalStatsContent(panel, selected, stats, hist);
        container.after(panel);

        var buttons = [
          {text: "Close", click: function() {jQuery(this).dialog().dialog("close");}}
        ];

        var dialog = panel.dialog({position: ['center', 'center'], buttons: buttons, title: series.label, width: 540, zIndex: 1010, stack: false});
        dialog.bind('dialogclose', function() {
          jQuery(this).dialog().dialog("destroy");
          jQuery(this).remove();
        });
      },

      /**
       * Add summary stats content to dialog
       */
      addSelectedIntervalStatsContent: function(panel, selected, stats, hist) {
        var series = selected[0].item.series;
        var st = jQuery('<div id="sosSelectedIntervalStatsTable" class="sos-selected-interval-stats-table"/>');
        var sp = jQuery('<div id="sosSelectedIntervalStatsPlot" class="sos-selected-interval-stats-plot" style="width: 300px; height: 150px;"/>');
        var fv = this.config.format.value;
        var tcontent = "";

        /* N.B.: It's crucial that any flot plot has width & height set.  The
                 above somewhat redundant style for the plot div is required
                 because IE & chrome don't see the CSS class definition before
                 the plot is generated, causing an uncaught exception */

        // Construct stats table
        for(var key in {min: 0, max: 0, mean: 0, median: 0, q1: 0, q3: 0, variance: 0, sd: 0}) {
          tcontent += '<tr>';
          tcontent += '<td class="sos-control-title">' + key + '</td>';
          tcontent += '<td> = ' + fv.formatter(parseFloat(stats[key]), fv.sciLimit, fv.digits) + '</td>';
          tcontent += '</tr>';
        }

        var table = '<table><tbody>';
        table += tcontent;
        table += '</tbody></table>';
        st.append(table);

        panel.append('<span class="sos-control-title">' + series.ordinateLabel + '</span>', '<hr></hr>');
        panel.append(st, sp);

        // Generate stats plot
        this.config.stats = this.config.stats || {};
        this.config.stats.series = [{data: hist.data}];
        this.config.stats.options = {
          grid: {borderWidth: 1},
          series: {
            color: series.color,
            bars: {
              show: true,
              barWidth: hist.binWidth
            }
          }
        };
        this.config.stats.object = jQuery.plot(sp, this.config.stats.series, this.config.stats.options);
      }
    });
  }

  /* Create the SOS.Plot namespace */
  if(typeof SOS.Plot === "undefined") {
    /**
     * SOS.Plot Class
     * Class for displaying a plot of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.Plot = OpenLayers.Class(SOS.Ui, {
      url: null,
      sos: null,
      offering: null,
      offeringId: null,
      observedProperty: null,
      foiId: null,
      startDatetime: null,
      endDatetime: null,
      relativeTime: null,
      config: null,
      CLASS_NAME: "SOS.Plot",

      /**
       * Constructor for a SOS.Plot object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.offering = null;
        this.offeringId = null;
        this.observedProperty = null;
        this.foiId = null;
        this.startDatetime = null;
        this.endDatetime = null;
        this.relativeTime = "today";
        this.config = {
          plot: {
            object: null,
            id: "sosPlot",
            series: [],
            options: {
              show: true,
              xaxis: {mode: "time", axisLabel: "Time", panRange: false},
              yaxis: {},
              xaxes: [],
              yaxes: [],
              haveCustomAxes: false,
              forceSingleAxis: false,
              labelTemplates: {
                label: "[%foiName%] [%name%]",
                headerLabel: "[%foiName%] [%name%] / [%uomTitle%]",
                ordinateLabel: "[%name%] / [%uomTitle%]"
              },
              selection: {mode: "x"},
              zoom: {interactive: true},
              pan: {interactive: true},
              grid: {borderWidth: 1, hoverable: true, clickable: true},
              legend: {show: true, backgroundOpacity: 0.5},
              series: {lines: {show: true}, points: {show: false}, bars: {show: false}}
            }
          },
          overview: {
            object: null,
            id: "sosPlotOverview",
            series: [],
            options: {
              show: false,
              xaxis: {ticks: [], mode: "time"},
              yaxis: {ticks: [], autoscaleMargin: 0.1},
              selection: {mode: "x"},
              grid: {borderWidth: 1},
              legend: {show: false},
              series: {lines: {show: true, lineWidth: 1}, points: {show: false}, bars: {show: false}, shadowSize: 0}
            }
          },
          format: {
            time: {
              formatter: SOS.Utils.jsTimestampToIso
            },
            value: {
              sciLimit: 0.1,
              digits: 2,
              formatter: SOS.Ui.prototype.formatValueFancy
            }
          },
          messages: {
            noDataForDateRange: "No data available for given dates."
          },
          mode: {append: false}
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.Plot object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the plot
       */
      setPlotOptions: function(options) {
        jQuery.extend(true, this.config.plot.options, options);
      },

      /**
       * Set options for the plot overview
       */
      setOverviewOptions: function(options) {
        jQuery.extend(true, this.config.overview.options, options);
      },

      /**
       * Set the given axis to logarithmic
       */
      setAxisLogarithmic: function(axis, base) {
        axis = axis || "yaxis";
        base = base || 10;
        this.config.plot.options[axis].transform = function(v) {return (v != 0 ? Math.log(v) / Math.log(base) : 0);};
        this.config.plot.options[axis].inverseTransform = function(v) {return Math.pow(base, v);};
        this.config.overview.options[axis].transform = this.config.plot.options[axis].transform;
        this.config.overview.options[axis].inverseTransform = this.config.plot.options[axis].inverseTransform;
      },

      /**
       * Set the given axis to reverse order
       */
      setAxisReverse: function(axis) {
        axis = axis || "yaxis";
        this.config.plot.options[axis].transform = this.config.plot.options[axis].inverseTransform = function(v) {return -v;};
        this.config.overview.options[axis].transform = this.config.plot.options[axis].transform;
        this.config.overview.options[axis].inverseTransform = this.config.plot.options[axis].inverseTransform;
      },

      /**
       * Set the given axis to reverse logarithmic
       */
      setAxisReverseLogarithmic: function(axis, base) {
        axis = axis || "yaxis";
        base = base || 10;
        this.config.plot.options[axis].transform = function(v) {return (v != 0 ? -(Math.log(v) / Math.log(base)) : 0);};
        this.config.plot.options[axis].inverseTransform = function(v) {return -Math.pow(base, v);};
        this.config.overview.options[axis].transform = this.config.plot.options[axis].transform;
        this.config.overview.options[axis].inverseTransform = this.config.plot.options[axis].inverseTransform;
      },
 
      /**
       * Generate the plot using this object's properties to query the SOS
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          jQuery.extend(true, this, options);
        }

        if(!this.haveValidCapabilitiesObject()) {
          this.getCapabilities(this._display);
        } else {
          this._display();
        }
      },

      /**
       * Add to pending plot using given additional options to query the SOS
       */
      add: function(options) {
        // Store parameters so they can be added to a base plot
        if(arguments.length > 0) {
          this.additional = this.additional || [];
          this.additional.push(options);
        }
      },

      /**
       * Get the observation data from the SOS according to this object's
       * properties, & then draw the plot
       */
      _display: function() {
        // Avoid incremental calls to this function on subsequent event trigger
        this.sos.unregisterUserCallback({event: "sosCapsAvailable", scope: this, callback: this._display});

        this._getOffering();

        if(this.haveValidOfferingObject()) {
          if(SOS.Utils.isValidObject(this.observedProperty)) {
            // If observed property is an array, fetch each in turn
            if(SOS.Utils.isArray(this.observedProperty)) {
              this.config.mode.append = true;
              var p = this.observedProperty[this.config.plot.series.length];

              if(p) {
                this.offering.filterObservedProperties(p);
              }
            } else {
              this.offering.filterObservedProperties(this.observedProperty);
            }
          }

          // The FOI will identify a given station in a multi-station offering
          if(SOS.Utils.isValidObject(this.foiId)) {
            this.offering.foiId = this.foiId;
          }
          this.determineObservationQueryTimeParameters();
          this.offering.registerUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawObservationData});
          this.offering.getObservations(this.startDatetime, this.endDatetime);
        }
      },

      /**
       * Store and then display the retrieved observation data
       */
      drawObservationData: function() {
        // Avoid incremental calls to this function on subsequent event trigger
        this.offering.unregisterUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawObservationData});

        // Add these data to the data series
        this.storeObservationData();

        // If observed property is an array, fetch each in turn
        if(SOS.Utils.isArray(this.observedProperty)) {
          if(this.config.plot.series.length < this.observedProperty.length) {
            this._display();
            return this.config.plot.series.length;
          }
        }

        // Display the data series
        this.draw();

        // Now we have the base plot, plot any additional data
        if(SOS.Utils.isValidObject(this.additional)) {
          this.addData();
        }
      },

      /**
       * Store the retrieved observation data
       */
      storeObservationData: function() {
        // Construct the data series
        var tables = this.constructDataSeries(this.offering, this.config.plot.options.labelTemplates);

        // We can add to an existing base plot or overwrite, dependent on mode
        if(this.config.mode.append) {
          this.config.plot.series = this.config.plot.series.concat(tables);
        } else {
          this.config.plot.series = tables;
        }
      },

      /**
       * Check whether we have some data to plot
       */
      haveRequiredData: function() {
        return this.getCountOfSeriesData(this.config.plot.series);
      },
 
      /**
       * Plot the given observation data
       */
      draw: function() {
        // Reset plot/overview if we've already set them up before
        this.resetBehaviour();

        // Set any last minute defaults if not already set
        this.applyDefaults();

        if(this.config.plot.options.show) {
          if(this.haveRequiredData()) {
            // Generate the plot
            this.config.plot.object = jQuery.plot(jQuery('#' + this.config.plot.id), this.config.plot.series, this.config.plot.options);

            // Optionally generate the plot overview
            if(this.config.overview.options.show) {
              this.drawOverview();
            }

            // Manage the plot's interactive behaviour
            this.setupBehaviour();

            // Optionally manage the plot overview behaviour
            if(this.config.overview.options.show) {
              this.setupOverviewBehaviour();
            }
          } else {
            var container = jQuery('#' + this.config.plot.id);
            container.html(this.formatInformationMessage(this.config.messages.noDataForDateRange));
          }
        }
      },

      /**
       * Apply any defaults where none have been specified or combination
       * of options is nonsensical
       */
      applyDefaults: function() {
        var options = this.config.plot.options;
        var series = this.config.plot.series;

        // Apply the current global series options to the current data series
        if(series.length > 0) {
          jQuery.extend(true, series[series.length-1], options.series);
        }
        options.grid = options.grid || {};

        if(options.grid.show === false) {
          options.xaxis.axisLabel = null;
          options.yaxis.axisLabel = null;
        } else {
          // We normally setup labels for y axes unless told otherwise
          if(!options.haveCustomAxes) {
            options.yaxes = options.yaxes || [];
              
            // We can force multiple data series to share a single axis
            if(options.forceSingleAxis) {
              if(series.length > 0) {
                options.yaxes = [];
                options.yaxis.axisLabel = series[0].ordinateLabel;
              }
            } else {
              for(var i = 0, len = series.length; i < len; i++) {
                options.yaxes[i] = {};
                series[i].yaxis = (i + 1);
                options.yaxes[i].axisLabel = series[i].ordinateLabel;
              }
            }
          }
        }
      },

      /**
       * Plot the given observation data as an overview plot
       */
      drawOverview: function() {
        var o = jQuery('#' + this.config.overview.id);

        // If overview div doesn't exist (the norm), create one on the fly
        if(o.length < 1) {
          var p = jQuery('#' + this.config.plot.id);
          o = jQuery('<div id="' + this.config.overview.id + '" class="sos-plot-overview"/>');
          p.after(o);
        }

        this.config.overview.series = this.config.plot.series;
        this.config.overview.object = jQuery.plot(o, this.config.overview.series, this.config.overview.options);
      },

      /**
       * Setup event handlers to manage the plot's behaviour
       */
      setupBehaviour: function() {
        var p = jQuery('#' + this.config.plot.id);
        var valueBox = jQuery('#' + this.config.plot.id + "ValueBox");

        // If valueBox div doesn't exist (the norm), create one on the fly
        if(valueBox.length < 1) {
          valueBox = jQuery('<div id="#' + this.config.plot.id + 'ValueBox" class="sos-plot-valuebox" style="display:none"/>');
          jQuery('body').after(valueBox);
        }

        // Show data coordinates (time, value) as mouse hovers over plot
        p.bind("plothover", {self: this}, function(evt, pos, item) {
          if(item) {
            var ft = evt.data.self.config.format.time;
            var fv = evt.data.self.config.format.value;
            // The small offsets avoid flickering when box is under mouse
            var x = pos.pageX + 20;
            var y = pos.pageY + 20;
            var time = item.datapoint[0];
            var datum = item.datapoint[1];
            var html = jQuery('<p><span class="sos-control-title">Time:</span> <span>' + ft.formatter(time) + '</span><br/><span class="sos-control-title">Value:</span> <span>' + fv.formatter(datum, fv.sciLimit, fv.digits) + ' ' + item.series.uomTitle + '</span></p>');

            valueBox.html(html);
            valueBox.css({
              position: "absolute",
              left: x + "px",
              top: y + "px",
              borderColor: item.series.color
            });
            valueBox.show();
          }
        });

        // Clear the value box when mouse leaves plot area
        p.bind("mouseout", function() {
          valueBox.hide();
        });

        // Show summary stats of a selected interval between two points
        p.bind("plotclick", {self: this}, function(evt, pos, item) {
          var self = evt.data.self;

          if(item) {
            self.config.plot.selected = self.config.plot.selected || [];
            self.config.plot.object.highlight(item.series, item.datapoint);
            self.config.plot.selected.push({pos: pos, item: item});

            // On first selection, grey-out all other curves on the plot
            if(self.config.plot.selected.length == 1) {
              self.greyOutSeries(item.seriesIndex);
            }

            if(self.config.plot.selected.length > 1) {
              if(self.config.plot.selected[1].item.seriesIndex == self.config.plot.selected[0].item.seriesIndex) {
                self.displaySelectedIntervalStats(p, self.config.plot.selected);
              }
              // Reinstate plot, including ungreying-out all other curves
              self.config.plot.object.unhighlight();
              delete self.config.plot.selected;
              self.update();
            }
          }
        });
      },

      /**
       * Setup event handlers to manage the plot overview's behaviour
       */
      setupOverviewBehaviour: function() {
        var p = jQuery('#' + this.config.plot.id);
        var o = jQuery('#' + this.config.overview.id);
        var plot = this.config.plot;
        var overview = this.config.overview;

        // These handlers connect the overview & the plot

        // Subset the plot from plot selection.  Overview can reinstate
        p.bind("plotselected", {self: this}, function(evt, ranges) {
          var self = evt.data.self;
          var plotOpts = plot.object.getOptions();
          jQuery.extend(plotOpts.xaxes[0], {min: ranges.xaxis.from, max: ranges.xaxis.to});
          self.update();
          plot.object.clearSelection(ranges);

          // Don't fire event on the overview to prevent eternal loop
          overview.object.setSelection(ranges, true);
        });
    
        // Subset the plot from overview selection.  Overview can reinstate
        o.bind("plotselected", {self: this}, function(evt, ranges) {
          plot.object.setSelection(ranges);
        });

        o.bind("plotunselected", {self: this}, function(evt) {
          var self = evt.data.self;
          var plotOpts = plot.object.getOptions();
          jQuery.extend(plotOpts.xaxes[0], {min: null, max: null});
          self.update();
        });
      },

      /**
       * Reset plot's event handlers
       */
      resetBehaviour: function() {
        var p = jQuery('#' + this.config.plot.id);
        var o = jQuery('#' + this.config.overview.id);

        if(p.length > 0) {
          p.unbind();
        }
        if(o.length > 0) {
          o.unbind();
        }
      },

      /**
       * Get the observation data from the SOS for each additional set of
       * query parameters, & then update the existing plot
       */
      addData: function() {
        if(SOS.Utils.isValidObject(this.additional)) {
          for(var i = 0, len = this.additional.length; i < len; i++) {
            jQuery.extend(true, this, this.additional[i]);
            this._getOffering();

            if(this.haveValidOfferingObject()) {
              if(SOS.Utils.isValidObject(this.observedProperty)) {
                this.offering.filterObservedProperties(this.observedProperty);
              }
              if(SOS.Utils.isValidObject(this.foiId)) {
                this.offering.foiId = this.foiId;
              }
              this.determineObservationQueryTimeParameters();
              this.offering.registerUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawAdditionalData});
              this.offering.getObservations(this.startDatetime, this.endDatetime);
            }
          }
        }
      },

      /**
       * Add the given observation data to an existing plot
       */
      drawAdditionalData: function() {
        // Avoid infinite loop as each additional listens for sosObsAvailable
        this.offering.unregisterUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawAdditionalData});

        // Construct the data series
        var tables = this.constructDataSeries(this.offering, this.config.plot.options.labelTemplates);

        if(this.config.plot.options.show) {
          if(tables && tables[0].data && tables[0].data.length > 0) {
            /* If base plot exists, we update, otherwise we generate plot */
            if(SOS.Utils.isValidObject(this.config.plot.object)) {
              this.config.plot.series = this.config.plot.series.concat(tables);
              this.update();
            } else {
              this.config.plot.series = tables;
              this.draw();
            }

            // Optionally update the plot overview also
            if(this.config.overview.options.show) {
              this.config.overview.series = this.config.plot.series;
              this.updateOverview();
            }
          }
        }
      },

      /**
       * Redraw an existing plot
       */
      update: function() {
        if(SOS.Utils.isValidObject(this.config.plot.object)) {
          this.config.plot.object.setData(this.config.plot.series);
          this.config.plot.object.setupGrid();
          this.config.plot.object.draw();
        }
      },

      /**
       * Redraw an existing overview plot
       */
      updateOverview: function() {
        if(SOS.Utils.isValidObject(this.config.overview.object)) {
          this.config.overview.object.setData(this.config.overview.series);
          this.config.overview.object.setupGrid();
          this.config.overview.object.draw();
        }
      },

      /**
       * Reset an existing plot (& overview)
       */
      reset: function() {
        this.resetSeries();
        this.resetOverviewSeries();
        this.resetBehaviour();
      },

      /**
       * Grey-out all except the given series on the plot.  Specifying a series
       * index of -1 will grey-out all series.  Call update() to reinstate
       * plot to original colours
       */
      greyOutSeries: function(seriesIndex) {
        var series = this.config.plot.object.getData();

        for(var i = 0, len = series.length; i < len; i++) {
          if(i != seriesIndex) {
            series[i].color = "rgb(240, 240, 240)";
          }
        }
        this.config.plot.object.setupGrid();
        this.config.plot.object.draw();
      },

      /**
       * Reset series of an existing plot
       */
      resetSeries: function() {
        this.config.plot.series = [];
      },

      /**
       * Reset series of an existing overview plot
       */
      resetOverviewSeries: function() {
        this.config.overview.series = [];
      },

      /**
       * Reset axes labels of an existing plot
       */
      resetAxesLabels: function() {
        this.config.plot.options.xaxes = [];
        this.config.plot.options.yaxes = [];
      }
    });
  }

  /* Create the SOS.Table namespace */
  if(typeof SOS.Table === "undefined") {
    /**
     * SOS.Table Class
     * Class for displaying a table of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.Table = OpenLayers.Class(SOS.Ui, {
      url: null,
      sos: null,
      offering: null,
      offeringId: null,
      observedProperty: null,
      foiId: null,
      startDatetime: null,
      endDatetime: null,
      relativeTime: null,
      config: null,
      CLASS_NAME: "SOS.Table",

      /**
       * Constructor for a SOS.Table object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.offering = null;
        this.offeringId = null;
        this.observedProperty = null;
        this.foiId = null;
        this.startDatetime = null;
        this.endDatetime = null;
        this.relativeTime = "today";
        this.config = {
          table: {
            object: null,
            id: "sosTable",
            series: [],
            options: {
              show: true,
              header: {},
              columns: {
                names: ["Time", "Value"]
              },
              labelTemplates: {
                label: "[%foiName%] [%name%]",
                headerLabel: "[%foiName%] [%name%] / [%uomTitle%]",
                ordinateLabel: "[%name%] / [%uomTitle%]"
              },
              scrollable: false
            }
          },
          overview: {
            object: null,
            id: "sosTableOverview",
            series: [],
            options: {
              show: false,
              xaxis: {ticks: [], mode: "time"},
              yaxis: {ticks: [], autoscaleMargin: 0.1},
              selection: {mode: "x"},
              grid: {borderWidth: 1},
              legend: {show: false},
              series: {lines: {show: true, lineWidth: 1}, points: {show: false}, bars: {show: false}, shadowSize: 0}
            }
          },
          format: {
            time: {
              formatter: SOS.Utils.jsTimestampToIso
            },
            value: {
              sciLimit: 0.1,
              digits: 2,
              formatter: SOS.Ui.prototype.formatValueFancy
            }
          },
          messages: {
            noDataForDateRange: "No data available for given dates."
          },
          mode: {append: false}
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.Table object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the table
       */
      setTableOptions: function(options) {
        jQuery.extend(true, this.config.table.options, options);
      },

      /**
       * Set options for the table overview
       */
      setOverviewOptions: function(options) {
        jQuery.extend(true, this.config.overview.options, options);
      },

      /**
       * Generate the table using this object's properties to query the SOS
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          jQuery.extend(true, this, options);
        }

        if(!this.haveValidCapabilitiesObject()) {
          this.getCapabilities(this._display);
        } else {
          this._display();
        }
      },

      /**
       * Add to pending table using given additional options to query the SOS
       */
      add: function(options) {
        // Store parameters so they can be added to a base table
        if(arguments.length > 0) {
          this.additional = this.additional || [];
          this.additional.push(options);
        }
      },

      /**
       * Get the observation data from the SOS according to this object's
       * properties, & then draw the table
       */
      _display: function() {
        // Avoid incremental calls to this function on subsequent event trigger
        this.sos.unregisterUserCallback({event: "sosCapsAvailable", scope: this, callback: this._display});

        this._getOffering();

        if(this.haveValidOfferingObject()) {
          if(SOS.Utils.isValidObject(this.observedProperty)) {
            // If observed property is an array, fetch each in turn
            if(SOS.Utils.isArray(this.observedProperty)) {
              this.config.mode.append = true;
              var p = this.observedProperty[this.config.table.series.length];

              if(p) {
                this.offering.filterObservedProperties(p);
              }
            } else {
              this.offering.filterObservedProperties(this.observedProperty);
            }
          }

          // The FOI will identify a given station in a multi-station offering
          if(SOS.Utils.isValidObject(this.foiId)) {
            this.offering.foiId = this.foiId;
          }
          this.determineObservationQueryTimeParameters();
          this.offering.registerUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawObservationData});
          this.offering.getObservations(this.startDatetime, this.endDatetime);
        }
      },

      /**
       * Store and then display the retrieved observation data
       */
      drawObservationData: function() {
        // Avoid incremental calls to this function on subsequent event trigger
        this.offering.unregisterUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawObservationData});

        // Add these data to the data series
        this.storeObservationData();

        // If observed property is an array, fetch each in turn
        if(SOS.Utils.isArray(this.observedProperty)) {
          if(this.config.table.series.length < this.observedProperty.length) {
            this._display();
            return this.config.table.series.length;
          }
        }

        // Display the data series
        this.draw();

        // Now we have the base table, add any additional data
        if(SOS.Utils.isValidObject(this.additional)) {
          this.addData();
        }
      },

      /**
       * Store the retrieved observation data
       */
      storeObservationData: function() {
        // Construct the data series
        var tables = this.constructDataSeries(this.offering, this.config.table.options.labelTemplates);

        // We can add to an existing base table or overwrite, dependent on mode
        if(this.config.mode.append) {
          this.config.table.series = this.config.table.series.concat(tables);
        } else {
          this.config.table.series = tables;
        }
      },

      /**
       * Check whether we have some data to display
       */
      haveRequiredData: function() {
        return this.getCountOfSeriesData(this.config.table.series);
      },

      /**
       * Display the given observation data
       */
      draw: function() {
        // Reset table/overview if we've already set them up before
        this.resetBehaviour();

        // Set any last minute defaults if not already set
        this.applyDefaults();

        if(this.config.table.options.show) {
          if(this.haveRequiredData()) {
            // Generate the table
            var t = jQuery('#' + this.config.table.id);
            this.clearTable(t);
            this.config.table.object = this.generateTable(t, this.config.table.series, this.config.table.options);

            // Optionally generate the table overview
            if(this.config.overview.options.show) {
              this.drawOverview();
            }

            // Manage the table's interactive behaviour
            this.setupBehaviour();

            // Optionally manage the table overview behaviour
            if(this.config.overview.options.show) {
              this.setupOverviewBehaviour();
            }
          } else {
            var container = jQuery('#' + this.config.table.id);
            container.html(this.formatInformationMessage(this.config.messages.noDataForDateRange));
          }
        }
      },

      /**
       * Apply any defaults where none have been specified or combination
       * of options is nonsensical
       */
      applyDefaults: function() {
        var options = this.config.overview.options;
        var series = this.config.table.series;

        /* N.B.: Although the table itself has no need for y-axes to be set,
                 it is required for the table overview plot */

        // Apply the current global series options to the current data series
        if(series.length > 0) {
          jQuery.extend(true, series[series.length-1], options.series);
        }
        options.yaxes = options.yaxes || [];

        // We can force multiple data series to share a single axis
        if(!options.forceSingleAxis) {
          // Specify which yaxis applies to which data series
          for(var i = 0, len = series.length; i < len; i++) {
            options.yaxes[i] = {};
            series[i].yaxis = (i + 1);
          }
        }
      },

      /**
       * Plot the given observation data as an overview plot
       */
      drawOverview: function() {
        var o = jQuery('#' + this.config.overview.id);

        // If overview div doesn't exist (the norm), create one on the fly
        if(o.length < 1) {
          var t = jQuery('#' + this.config.table.id);
          o = jQuery('<div id="' + this.config.overview.id + '" class="sos-plot-overview"/>');
          t.after(o);
        }

        this.config.overview.series = this.config.table.series;
        this.config.overview.object = jQuery.plot(o, this.config.overview.series, this.config.overview.options);
      },

      /**
       * Setup event handlers to manage the table's behaviour
       */
      setupBehaviour: function() {
        var t = jQuery('#' + this.config.table.id);

        // Setup custom events for the table
        this.setupTableEventTriggers(t, "td");

        /* Highlight datetime & value cells as mouse moves over table.  The
           selecting flag determines between drag selection or discrete click */
        t.delegate("td", "mouseover mouseout", {self: this}, function(evt) {
          evt.data.self.config.table.selecting ? evt.data.self.highlightCellGroup(this) : evt.data.self.toggleHighlightCellGroup(this);
        });
 
        // Show summary stats of a selected interval between two points
        t.bind("tableclick", {self: this}, function(evt) {
          var self = evt.data.self;
          var item = self.eventToItem(evt);
          delete self.config.table.selecting;

          if(item) {
            self.config.table.selected = self.config.table.selected || [];
            self.highlightSelectedCellGroup(evt.target);
            self.config.table.selected.push({item: item});

            // On first selection, grey-out all other series on the table
            if(self.config.table.selected.length == 1) {
              self.greyOutSeries(item.seriesIndex);
            }

            if(self.config.table.selected.length > 1) {
              if(self.config.table.selected[1].item.seriesIndex == self.config.table.selected[0].item.seriesIndex) {
                self.displaySelectedIntervalStats(t, self.config.table.selected);
              }
              // Reinstate table, including ungreying-out all other series
              self.unhighlightSelected();
              delete self.config.table.selected;
              self.update();
            }
          }
        });
      },

      /**
       * Setup event handlers to manage the table overview's behaviour
       */
      setupOverviewBehaviour: function() {
        var t = jQuery('#' + this.config.table.id);
        var o = jQuery('#' + this.config.overview.id);
        var overview = this.config.overview;

        // These handlers connect the overview & the table

        // Subset the table from overview selection.  Overview can reinstate
        o.bind("plotselected", {self: this}, function(evt, ranges) {
          evt.data.self.subset(ranges.xaxis.from, ranges.xaxis.to);

          // Don't fire event on the overview to prevent eternal loop
          overview.object.setSelection(ranges, true);
        });

        // Drag selection handlers for table
        t.delegate("td", "tableselecting", {self: this}, function(evt) {
          evt.data.self.config.table.selecting = true;
          evt.data.self.highlightSelectedCellGroup(evt.target);
        });

        t.delegate("td", "tableshiftclickselecting", {self: this}, function(evt) {
          evt.data.self.highlightSelectedCellGroup(evt.target);
        });

        // Subset the table from table selection.  Overview can reinstate
        t.delegate("td", "tableselected", {self: this}, function(evt, selection) {
          var self = evt.data.self;
          delete self.config.table.selecting;

          if(selection) {
            var ranges = self.selectionToRanges(selection);

            if(selection.items.length > 1) {
              self.subset(ranges.xaxis.from, ranges.xaxis.to);
              overview.object.setSelection(ranges, true);
              self.clearSelectionHighlighting();
            }
          }
        });

        o.bind("plotunselected", {self: this}, function(evt) {
          evt.data.self.update();
        });
      },

      /**
       * Setup handling for custom event triggers on a table
       */
      setupTableEventTriggers: function(t, selectors) {
        var selection = {active: false, start: null, end: null};

        /* Determine between a click or a dragged selection.  A shift-click
           at two distinct locations is the same as a dragged selection */
        t.delegate(selectors, "mousedown mouseup click", {self: this}, function(evt) {
          var self = evt.data.self;
          var elem = jQuery(evt.target);

          if(evt.type == "mousedown" && !evt.shiftKey) {
            selection.active = true;
            selection.start = evt;
            evt.preventDefault();
            elem.trigger("tableselecting");
          }
          if(evt.type == "mouseup" && !evt.shiftKey) {
            selection.active = false;
            selection.end = evt;

            if(selection.start.target == selection.end.target) {
              elem.trigger("tableclick");
            } else {
              var items = [];
              items.push({item: self.eventToItem(selection.start)});
              items.push({item: self.eventToItem(selection.end)});

              if(items[1].item.dataIndex < items[0].item.dataIndex) {
                items.reverse();
              }
              elem.trigger("tableselected", {items: items});
            }
          }
          if(evt.type == "click" && evt.shiftKey) {
            if(!selection.active) {
              selection.active = true;
              selection.start = evt;
              evt.preventDefault();
              elem.trigger("tableshiftclickselecting");
            } else {
              selection.active = false;
              selection.end = evt;

              var items = [];
              items.push({item: self.eventToItem(selection.start)});
              items.push({item: self.eventToItem(selection.end)});

              if(items[1].item.dataIndex < items[0].item.dataIndex) {
                items.reverse();
              }
              elem.trigger("tableselected", {items: items});
            }
          }

          return false;
        });
      },

      /**
       * Reset table's event handlers
       */
      resetBehaviour: function() {
        var t = jQuery('#' + this.config.table.id);
        var o = jQuery('#' + this.config.overview.id);

        if(t.length > 0) {
          t.unbind();
        }
        if(o.length > 0) {
          o.unbind();
        }
      },

      /**
       * Get the observation data from the SOS for each additional set of
       * query parameters, & then update the existing table
       */
      addData: function() {
        if(SOS.Utils.isValidObject(this.additional)) {
          for(var i = 0, len = this.additional.length; i < len; i++) {
            jQuery.extend(true, this, this.additional[i]);
            this._getOffering();

            if(this.haveValidOfferingObject()) {
              if(SOS.Utils.isValidObject(this.observedProperty)) {
                this.offering.filterObservedProperties(this.observedProperty);
              }
              if(SOS.Utils.isValidObject(this.foiId)) {
                this.offering.foiId = this.foiId;
              }
              this.determineObservationQueryTimeParameters();
              this.offering.registerUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawAdditionalData});
              this.offering.getObservations(this.startDatetime, this.endDatetime);
            }
          }
        }
      },

      /**
       * Add the given observation data to an existing table
       */
      drawAdditionalData: function() {
        // Avoid infinite loop as each additional listens for sosObsAvailable
        this.offering.unregisterUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawAdditionalData});

        // Construct the data series
        var tables = this.constructDataSeries(this.offering, this.config.table.options.labelTemplates);

        if(this.config.table.options.show) {
          if(tables && tables[0].data && tables[0].data.length > 0) {
            /* If base table exists, we update, otherwise we generate table */
            if(SOS.Utils.isValidObject(this.config.table.object)) {
              this.config.table.series = this.config.table.series.concat(tables);
              this.update();
            } else {
              this.config.table.series = tables;
              this.draw();
            }

            // Optionally update the plot overview also
            if(this.config.overview.options.show) {
              this.config.overview.series = this.config.table.series;
              this.updateOverview();
            }
          }
        }
      },

      /**
       * Redraw an existing table
       */
      update: function() {
        if(SOS.Utils.isValidObject(this.config.table.object)) {
          this.config.table.object.html("");
          this.generateTable(this.config.table.object, this.config.table.series, this.config.table.options);
        }
      },

      /**
       * Redraw an existing overview plot
       */
      updateOverview: function() {
        if(SOS.Utils.isValidObject(this.config.overview.object)) {
          this.config.overview.object.setData(this.config.overview.series);
          this.config.overview.object.setupGrid();
          this.config.overview.object.draw();
        }
      },

      /**
       * Reset an existing table (& overview)
       */
      reset: function() {
        this.resetSeries();
        this.resetOverviewSeries();
        this.resetBehaviour();
      },

      /**
       * Grey-out all except the given series on the table.  Specifying a series
       * index of -1 will grey-out all series.  Call update() to reinstate
       * table to original colours
       */
      greyOutSeries: function(seriesIndex) {
        var series = this.config.table.series;
        var style = {color: "rgb(240, 240, 240)"};

        for(var i = 0, slen = series.length; i < slen; i++) {
          if(i != seriesIndex) {
            jQuery('th[class^="sos-table"][id="sl' + i + '"]').css(style);
            jQuery('th[class^="sos-table"][id="ch' + i + '"]').css(style);
            jQuery('td[class^="sos-table"][id^="' + i + '"]').css(style);
            jQuery('td[class^="sos-table"][id^="' + i + '"]').removeClass("sos-table-highlight-selected");
          }
        }
      },

      /**
       * Reset series of an existing table
       */
      resetSeries: function() {
        this.config.table.series = [];
      },

      /**
       * Reset series of an existing overview plot
       */
      resetOverviewSeries: function() {
        this.config.overview.series = [];
      },

      /**
       * Reset header labels of an existing table
       */
      resetHeaderLabels: function() {
        var series = this.config.table.series || [];

        if(SOS.Utils.isValidObject(this.config.table.options.header.title)) {
          this.config.table.options.header.title = null;
        }

        for(var i = 0, len = series.length; i < len; i++) {
          series[i].headerLabel = null;
        }
      },

      /**
       * Subset an existing table
       */
      subset: function(from, to) {
        var subset = this.subsetDataSeries(this.config.table.series, from, to);

        if(subset) {
          this.config.table.object.html("");
          this.generateTable(jQuery('#' + this.config.table.id), subset, this.config.table.options);
        }
      },

      /**
       * Clear down the placeholder element for the table
       */
      clearTable: function(t) {
        t.html("");
      },

      /**
       * Generate a table of the given observation data
       */
      generateTable: function(t, series, options) {
        var tcontent = "";
        var lengths = [];
        var ft = this.config.format.time;
        var fv = this.config.format.value;
        options.columns.names = options.columns.names || ["Time", "Value"];
        var clen = options.columns.names.length;

        for(var i = 0, len = series.length; i < len; i++) {
          lengths.push(series[i].data.length);
        }
        var maxrows = Math.max.apply(null, lengths);
 
        tcontent += '<thead class="sos-table">';
        tcontent += '<tr class="sos-table">';

        // Series header label
        for(var i = 0, len = series.length; i < len; i++) {
          tcontent += '<th id="sl' + i + '" class="sos-table" colspan="' + clen + '">' + series[i].headerLabel + '</th>';
        }
        tcontent += '</tr>';
        tcontent += '<tr class="sos-table">';

        // Per series column headings
        for(var i = 0, slen = series.length; i < slen; i++) {
          for(var j = 0, clen = options.columns.names.length; j < clen; j++) {
            tcontent += '<th id="ch' + i + '" class="sos-table">' + options.columns.names[j] + '</th>';
          }
        }
        tcontent += '</tr>';
        tcontent += '</thead>';
        tcontent += '<tfoot class="sos-table"/>';
        tcontent += '<tbody class="sos-table">';

        // Per series data
        for(var i = 0; i < maxrows; i++) {
          var cssClass = (i % 2 == 0 ? "sos-table-even" : "sos-table-odd");
          tcontent += '<tr class="' + cssClass + '">';

          for(var j = 0, slen = series.length; j < slen; j++) {
            if(SOS.Utils.isValidObject(series[j].data[i])) {
              for(var k = 0, dlen = series[j].data[i].length; k < dlen; k++) {
                var id = j + "." + i + "." + k, datum;

                // Format the datetime or value accordingly
                if(k == 0) {
                  datum = ft.formatter(series[j].data[i][k]);
                } else {
                  datum = (SOS.Utils.isNumber(series[j].data[i][k]) ? fv.formatter(parseFloat(series[j].data[i][k]), fv.sciLimit, fv.digits) : series[j].data[i][k]);
                }
                tcontent += '<td class="sos-table" id="' + id + '">' + datum + '</td>';
              }
            } else {
              tcontent += '<td></td><td></td>';
            }
          }
          tcontent += '</tr>';
        }
        tcontent += '</tbody>';

        var tableText = '<table class="sos-table">';
        tableText += (SOS.Utils.isValidObject(options.header.title) ? '<caption class="sos-table">' + options.header.title + '</caption>' : '');
        tableText += tcontent;
        tableText += '</table>';
        var table = jQuery(tableText);

        // Optionally the table can be scrollable
        if(options.scrollable) {
          table.addClass("sos-table-scrollable");
        }
        t.append(table);

        return t;
      },

      /**
       * Generate a plain non-HTML table of the given observation data
       */
      generatePlainDataTable: function(t, series, options) {
        var tcontent = "";
        var lengths = [];
        var ft = this.config.format.time;
        var fv = this.config.format.value;
        var commentCharacter = options.commentCharacter || '#';
        var columnSeparator = options.columnSeparator || ',';
        var rowSeparator = options.rowSeparator || '\n';
        options.columns.names = options.columns.names || ["Time", "Value"];

        // The value formatter should be plain non-HTML
        fv.formatter = SOS.Ui.prototype.formatValueSimple;

        for(var i = 0, len = series.length; i < len; i++) {
          lengths.push(series[i].data.length);
        }
        var maxrows = Math.max.apply(null, lengths);

        tcontent += commentCharacter;

        // Series header label (with plain non-HTML UOMs)
        for(var i = 0, len = series.length; i < len; i++) {
          tcontent += series[i].label + (series[i].uom.length > 0 ? " / " + series[i].uom : "");

          if(i < len - 1) {
            tcontent += columnSeparator;
          }
        }
        tcontent += rowSeparator;
        tcontent += commentCharacter;

        // Per series column headings
        for(var i = 0, slen = series.length; i < slen; i++) {
          for(var j = 0, clen = options.columns.names.length; j < clen; j++) {
            tcontent += options.columns.names[j];

            if(i < slen - 1 || j < clen - 1) {
              tcontent += columnSeparator;
            }
          }
        }
        tcontent += rowSeparator;

        // Per series data
        for(var i = 0; i < maxrows; i++) {
          for(var j = 0, slen = series.length; j < slen; j++) {
            if(SOS.Utils.isValidObject(series[j].data[i])) {
              for(var k = 0, dlen = series[j].data[i].length; k < dlen; k++) {
                var id = j + "." + i + "." + k, datum;

                // Format the datetime or value accordingly
                if(k == 0) {
                  datum = ft.formatter(series[j].data[i][k]);
                } else {
                  datum = (SOS.Utils.isNumber(series[j].data[i][k]) ? fv.formatter(parseFloat(series[j].data[i][k]), fv.sciLimit, fv.digits) : series[j].data[i][k]);
                }
                tcontent += datum;

                if(k < dlen - 1) {
                  tcontent += columnSeparator;
                }
              }
            } else {
              tcontent += columnSeparator;
            }
            if(j < slen - 1) {
              tcontent += columnSeparator;
            }
          }
          tcontent += rowSeparator;
        }

        var tableText = '';
        tableText += (SOS.Utils.isValidObject(options.header.title) ? options.header.title : '');
        tableText += tcontent;

        t = t || {};
        t.tableText = tableText;

        return t;
      },

      /**
       * Convert an event object to a flot-item-like object
       */
      eventToItem: function(evt) {
        var item;

        if(evt.target) {
          var a = evt.target.id.split(".");

          // Only construct the item if a valid data target was clicked on
          if(a.length >= 2) {
            item = {datapoint: [], dataIndex: 0, series: {}, seriesIndex: 0, pageX: 0, pageY: 0};
            item.seriesIndex = parseInt(a[0], 10);
            item.dataIndex = parseInt(a[1], 10);

            if(SOS.Utils.isValidObject(this.config.table.series)) {
              item.series = this.config.table.series[item.seriesIndex];
              item.datapoint = item.series.data[item.dataIndex];
            }
            item.pageX = evt.pageX;
            item.pageY = evt.pageY;
          }
        }

        return item;
      },

      /**
       * Convert a selection object to a flot-ranges-like object
       */
      selectionToRanges: function(selection) {
        var ranges;

        if(selection && selection.items) {
          if(selection.items.length > 1) {
            var from = selection.items[0].item.datapoint[0];
            var to = selection.items[1].item.datapoint[0];
            ranges = {xaxis: {from: from, to: to}};
          }
        }

        return ranges;
      },

      /**
       * Highlight a given cell in the table
       */
      highlight: function(elem) {
        jQuery(elem).addClass("sos-table-highlight");
      },

      /**
       * Toggle highlight on a given cell in the table
       */
      toggleHighlight: function(elem) {
        jQuery(elem).toggleClass("sos-table-highlight");
      },

      /**
       * Unhighlight any highlighted cells in the table
       */
      unhighlight: function() {
        if(SOS.Utils.isValidObject(this.config.table.object)) {
          this.config.table.object.find("td").removeClass("sos-table-highlight");
        }
      },

      /**
       * Highlight a given datetime & value cell-group in the table
       */
      highlightCellGroup: function(elem) {
        var cell = jQuery(elem);
        this.highlight(cell.closest('td').parent()[0].children);
      },

      /**
       * Toggle highlight on a given datetime & value cell-group in the table
       */
      toggleHighlightCellGroup: function(elem) {
        var cell = jQuery(elem);
        this.toggleHighlight(cell.closest('td').parent()[0].children);
      },

      /**
       * Highlight a given selected cell in the table
       */
      highlightSelected: function(elem) {
        jQuery(elem).addClass("sos-table-highlight-selected");
      },

      /**
       * Unhighlight any selected cells in the table
       */
      unhighlightSelected: function() {
        if(SOS.Utils.isValidObject(this.config.table.object)) {
          this.config.table.object.find("td").removeClass("sos-table-highlight-selected");
        }
      },

      /**
       * Highlight a given selected datetime & value cell-group in the table
       */
      highlightSelectedCellGroup: function(elem) {
        var cell = jQuery(elem);
        this.highlightSelected(cell.closest('td').parent()[0].children);
      },

      /**
       * Clear all highlighting from all selected cells in the table
       */
      clearSelectionHighlighting: function() {
        this.unhighlightSelected();
        this.unhighlight();
      }
    });
  }

  /* Create the SOS.Map namespace */
  if(typeof SOS.Map === "undefined") {
    /**
     * SOS.Map Class
     * Class for displaying a map of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.Map = OpenLayers.Class(SOS.Ui, {
      url: null,
      sos: null,
      config: null,
      CLASS_NAME: "SOS.Map",

      /**
       * Constructor for a SOS.Map object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.config = {
          map: {
            object: null,
            id: "sosMap",
            options: {
              /* Use centre and zoom, or params.restrictedExtent to set the
                 map's initial view */
              defaultProjection: new OpenLayers.Projection("EPSG:4326"),
              centre: new OpenLayers.LonLat(0, 0),
              zoom: 0,
              params: {
                projection: "EPSG:4326",
                displayProjection: new OpenLayers.Projection("EPSG:4326")
              }
            }
          },
          overview: {
            options: {
              show: false,
              params: {
              }
            }
          },
          format: {
            time: {
              formatter: SOS.Utils.jsTimestampToIso
            },
            value: {
              sciLimit: 0.1,
              digits: 2,
              formatter: SOS.Ui.prototype.formatValueFancy
            }
          },
          baseLayer: {
            object: null,
            id: "sosMapBaseLayer",
            options: {
              useOsm: false,      // Set true to use OSM instead of WMS
              label: "OpenLayers WMS",
              url: "http://vmap0.tiles.osgeo.org/wms/vmap0?",
              params: {
                layers: "basic"
              },
              extras: {
              }
            }
          },
          featureOfInterestLayer: {
            object: null,
            id: "sosMapFeatureOfInterestLayer",
            options: {
              label: "Feature Of Interest",
              pointStyle: new OpenLayers.Style({
                "pointRadius": 5,
                "fillColor": "#F80000",
                "strokeWidth": 1,
                "label": "${name}",
                "fontSize": "12px",
                "fontFamily": "Courier New, monospace",
                "fontWeight": "bold",
                "labelAlign": "rb",
                "labelXOffset": -10,
                "labelOutlineColor": "white",
                "labelOutlineWidth": 3
              }),
              crs: {
                format: {
                  /* Optionally specifies coordinate order in layer CRS.
                     If null, relies on OpenLayers defaults.
                     If true, order will be xy (e.g., lon/lat).
                     If false, order will be yx */
                  xy: null
                }
              },
              displayLatestObservations: false
            }
          },
          latestObservationsPopup: {
            active: true,
            caption: "Latest Values",
            columnHeadings: [
              "Observed Property",
              "Time",
              "Value"
            ]
          }
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.Map object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the map
       */
      setMapOptions: function(options) {
        jQuery.extend(true, this.config.map.options, options);
      },

      /**
       * Set options for the map overview
       */
      setOverviewOptions: function(options) {
        jQuery.extend(true, this.config.overview.options, options);
      },

      /**
       * Set options for the base layer
       */
      setBaseLayerOptions: function(options) {
        jQuery.extend(true, this.config.baseLayer.options, options);
      },

      /**
       * Set options for the feature-of-interest (FOI) layer
       */
      setFeatureOfInterestLayerOptions: function(options) {
        jQuery.extend(true, this.config.featureOfInterestLayer.options, options);
      },

      /**
       * Generate the map using this object's properties to query the SOS
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          jQuery.extend(true, this, options);
        }

        if(!this.haveValidCapabilitiesObject()) {
          this.getCapabilities(this._display);
        } else {
          this._display();
        }
      },

      /**
       * Get data from the SOS according to this object's properties, & then
       * draw the map
       */
      _display: function() {
        this.config.isInitLoad = true;
        this.initMap();
        this.initOverviewMap();
        this.initBaseLayer();
        this.initView();
        this.initFeatureOfInterestLayer();
        this.config.isInitLoad = false;
      },
 
      /**
       * Initialise the map
       */
      initMap: function() {
        var m = jQuery('#' + this.config.map.id);

        // If map div doesn't exist, create one on the fly
        if(m.length < 1) {
          m = jQuery('<div id="' + this.config.map.id + '" class="sos-map"/>');
          jQuery('body').append(m);
        }

        // Setup the map object & its controls
        var map = new OpenLayers.Map(this.config.map.id, this.config.map.options.params);

        map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.MousePosition());

        this.config.map.object = map;
      },

      /**
       * Initialise the map overview map
       */
      initOverviewMap: function() {
        var map = this.config.map.object;

        // Optionally generate the map overview map
        if(this.config.overview.options.show) {
          var params = this.config.overview.options.params || {};
          map.addControl(new OpenLayers.Control.OverviewMap(params));
        }
      },
 
      /**
       * Initialise the map base layer
       */
      initBaseLayer: function() {
        var map = this.config.map.object;
        var baseLayer;

        // Setup the map's base layer, and its controls
        if(this.config.baseLayer.options.useOsm) {
          baseLayer = new OpenLayers.Layer.OSM();
        } else {
          baseLayer = new OpenLayers.Layer.WMS(this.config.baseLayer.options.label, this.config.baseLayer.options.url, this.config.baseLayer.options.params, this.config.baseLayer.options.extras);
        }
        map.addLayers([baseLayer]);

        this.config.baseLayer.object = baseLayer;
      },

      /**
       * Initialise the map view
       */
      initView: function() {
        var map = this.config.map.object;
        var centre = this.config.map.options.centre || new OpenLayers.LonLat(0, 0);
        var zoom = this.config.map.options.zoom || 0;

        if(zoom) {
          map.setCenter(centre, zoom);
        } else {
          map.zoomToMaxExtent();
        }
      },
  
      /**
       * Initialise the feature-of-interest layer
       */
      initFeatureOfInterestLayer: function() {
        var styleMap = new OpenLayers.StyleMap(this.config.featureOfInterestLayer.options.pointStyle);

        var protocolFormatOptions = {
          internalProjection: this.config.map.object.getProjectionObject(),
          externalProjection: this.config.map.options.defaultProjection
        };

        /* Allows the coordinate order in layer CRS to be explicitly
           specified.  For example, the OpenLayers default for EPSG:4326 is
           false = yx = lat/lon, but some SOS instances return lon/lat */
        if(SOS.Utils.isValidObject(this.config.featureOfInterestLayer.options.crs.format.xy)) {
          protocolFormatOptions.xy = this.config.featureOfInterestLayer.options.crs.format.xy;
        }

        // Query FOIs from the SOS and present them as a vector layer
        var layer = new OpenLayers.Layer.Vector(this.config.featureOfInterestLayer.options.label, {
          strategies: [new OpenLayers.Strategy.Fixed()],
          protocol: new OpenLayers.Protocol.SOS({
            formatOptions: protocolFormatOptions,
            url: this.sos.config.post.url,
            fois: this.sos.getFeatureOfInterestIds()
          }),
          styleMap: styleMap
        });
        this.config.map.object.addLayer(layer);

        // Setup behaviour for this layer
        var ctrl = new OpenLayers.Control.SelectFeature(layer, {
          scope: this,
          onSelect: this.featureOfInterestSelectHandler
        });
        this.config.map.object.addControl(ctrl);
        ctrl.activate();

        this.config.featureOfInterestLayer.object = layer;
      },

      /**
       * Setup behaviour for when user clicks on a feature-of-interest (FOI)
       */
      featureOfInterestSelectHandler: function(feature) {
        var item = {
          foi: {
            id: feature.attributes.id,
            name: feature.attributes.name,
            geometry: feature.geometry
          }
        };

        // Store each selected item (FOI)
        this.config.map.selected = [];
        this.config.map.selected.push({item: item});

        // Optionally show this FOI's latest observation values in a popup
        if(this.config.featureOfInterestLayer.options.displayLatestObservations) {
          this.sos.registerUserCallback({event: "sosLatestObsAvailable", scope: this, callback: this.displayLatestObservations});
          this.sos.getLatestObservationsForFeatureOfInterestId(item.foi.id);
        }

        // For external listeners (application-level plumbing)
        this.sos.events.triggerEvent("sosMapFeatureOfInterestSelect");
      },

      /**
       * Display the latest observation values for the selected FOI
       */
      displayLatestObservations: function() {
        var map = this.config.map.object;

        if(SOS.Utils.isValidObject(this.config.map.selected)) {
          var feature = this.config.map.selected[0].item.foi;

          // Remove any existing popups (works but is a bit blunt!)
          for(var i = 0, len = map.popups.length; i < len; i++) {
            map.removePopup(map.popups[i]);
          };

          // Display latest observations table for this feature in a popup
          var popup = new OpenLayers.Popup.FramedCloud("sosLatestObservations",
            feature.geometry.getBounds().getCenterLonLat(),
            null,
            this.populateMultivariateTable(this.sos),
            null,
            true,
            function(e) {
              this.hide();
              OpenLayers.Event.stop(e);
              // Unselect so popup can be shown again
              this.map.getControlsByClass('OpenLayers.Control.SelectFeature')[0].unselectAll();
          });
          map.addPopup(popup);
        }
      },

      /**
       * Construct a table of data from multiple variables
       */
      populateMultivariateTable: function(sos) {
        var tcontent = "", html = "";
        var ft = this.config.format.time;
        var fv = this.config.format.value;

        if(SOS.Utils.isValidObject(sos)) {
          for(var i = 0, len = sos.getCountOfObservations(); i < len; i++) {
            var ob = sos.getObservationRecord(i);
            var cssClass = (i % 2 == 0 ? "sos-table-even" : "sos-table-odd");
            tcontent += '<tr class="' + cssClass + '">';
            tcontent += '<td class="sos-table">' + ob.observedPropertyTitle + '</td>';
            tcontent += '<td class="sos-table">' + ft.formatter(ob.time) + '</td>';
            tcontent += '<td class="sos-table">' + fv.formatter(ob.result.value, fv.sciLimit, fv.digits) + ' ' + ob.uomTitle + '</td>';
            tcontent += '</tr>';
          }
          html += '<table class="sos-table sos-embedded-table">';
          html += '<caption class="sos-table">' + this.config.latestObservationsPopup.caption + '</caption>';
          html += '<thead class="sos-table">';
          html += '<tr class="sos-table">';
          for(var j = 0, len = this.config.latestObservationsPopup.columnHeadings.length; j < len; j++) {
            html += '<th class="sos-table">' + this.config.latestObservationsPopup.columnHeadings[j] + '</th>';
          }
          html += '</tr>';
          html += '</thead>';
          html += '<tfoot/>';
          html += '<tbody>';
          html += tcontent;
          html += '</tbody>';
          html += '</table>';
        }

        return html;
      }
    });
  }

  /* Create the SOS.Menu namespace */
  if(typeof SOS.Menu === "undefined") {
    /**
     * SOS.Menu Class
     * Class for displaying a menu of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.Menu = OpenLayers.Class(SOS.Ui, {
      url: null,
      sos: null,
      config: null,
      CLASS_NAME: "SOS.Menu",

      /**
       * Constructor for a SOS.Menu object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.config = {
          menu: {
            object: null,
            id: "sosMenu",
            entries: [],
            step: 0,
            options: {
              tabs: {
                offerings: {
                  active: true,
                  label: "Offerings",
                  prompt: "Please select a Feature Of Interest from the Map"
                },
                observedProperties: {
                  active: true,
                  label: "Observed Properties",
                  prompt: "Please select an Offering"
                },
                controls: {
                  active: true,
                  label: "Controls",
                  prompt: "Please select an Observed Property"
                }
              },
              listBoxes: {
                multiple: false,
                size: 5,
                useToolTip: true,
                useSelectBox: false
              },
              datePickers: {
                // N.B.: This is a 4-digit year
                dateFormat: "yy-mm-dd",
                autoSize: true,
                changeYear: true,
                changeMonth: true,
                onSelect: function(s, ui) {jQuery(this).trigger('change');}
              },
              offerings: {
                useFqn: false
              },
              observedProperties: {
                /* Toggle fully-qualified name (FQN) in menus.  For example:
                   FQN: "urn:ogc:def:phenomenon:OGC:1.0.30:air_temperature"
                   Name: "Air Temperature"
                   If useFqn is false, we use Name, otherwise FQN */
                useFqn: false
              },
              createNewItem: false,
              promptForSelection: true,
              plotTableControlsSection: {
                active: true,
                sectionLabel: "Plot / Table",
                dateLabel: "Date Range",
                addToLabel: "Add To Existing",
                downloadData: {
                  active: true,
                  icon: "ui-icon-disk",
                  label: "Download Data",
                  prompt: "Format the data for download",
                  errorMessage: "No data selected.  You must plot/tabulate the data before you can download them.",
                  commentCharacterLabel: "Comment Character",
                  commentCharacters: ["#", "//", "--", "%", ";"],
                  columnSeparatorLabel: "Column Separator",
                  columnSeparators: [",", " ", "|", "\t"],
                  rowSeparatorLabel: "Row Separator",
                  rowSeparators: ["\n", "\r\n", "\r", ";"]
                }
              },
              searchOfferings: {
                active: true,
                label: "Search",
                prompt: ""
              },
              listAllOfferings: {
                active: true,
                label: "List all Offerings",
                prompt: "or alternatively"
              }
            }
          },
          format: {
            time: {
              formatter: SOS.Utils.jsTimestampToIso
            },
            value: {
              sciLimit: 0.1,
              digits: 2,
              formatter: SOS.Ui.prototype.formatValueFancy
            }
          }
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.Menu object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the menu
       */
      setMenuOptions: function(options) {
        jQuery.extend(true, this.config.menu.options, options);
      },

      /**
       * Set the menu initially empty (waiting for an FOI to be provided)
       */
      setInitialViewBlank: function() {
        this.config.menu.step = -1;
      },

      /**
       * Generate the menu using this object's properties to query the SOS
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          jQuery.extend(true, this, options);
        }

        if(!this.haveValidCapabilitiesObject()) {
          this.getCapabilities(this._display);
        } else {
          this._display();
        }
      },

      /**
       * Get data from the SOS according to this object's properties, & then
       * draw the menu
       */
      _display: function() {
        this.constructMenu();

        // We can use the step property to determine initial menu view
        if(this.config.menu.step == -1) {
          this.config.menu.step = 0;
          this.displayBlankMenu();
        } else if(this.config.menu.step == 1) {
          this.config.menu.step = 2;
          this.displayObservedProperties();
        } else if(this.config.menu.step == 2) {
          this.config.menu.step = 3;
          if(SOS.Utils.isValidObject(this.config.menu.options.tabs.controls) && this.config.menu.options.tabs.controls.active) {
            this.displayControls();
          }
        } else {
          this.config.menu.step = 1;
          this.displayOfferings();
        }
        if(SOS.Utils.isValidObject(this.config.menu.options.tabs.controls) && this.config.menu.options.tabs.controls.active) {
          this.initControls();
        }
      },

      /**
       * Display an initial empty menu (waiting for an FOI to be provided)
       */
      displayBlankMenu: function() {
        this.initBlankMenu();
      },

      /**
       * Display the offerings
       */
      displayOfferings: function() {
        var tab = jQuery('#' + this.config.menu.id + 'OfferingsTab');
        this.constructOfferingsEntries();
        this.initMenu(tab);
        this.setupOfferingsBehaviour();
        this.constructOfferingsTabControls();
        this.promptForSelection(tab);
      },

      /**
       * Construct the offerings menu entries
       */
      constructOfferingsEntries: function(options) {
        var ids = [], names = [];
        var options = options || {filterOnFOI: true};
        this.config.menu.entries = [];
        var item = this.getCurrentItem();

        /* If an FOI was selected, then only get offerings for that FOI.
           Otherwise we get all offerings */
        if(options.filterOnFOI && SOS.Utils.isValidObject(item) && SOS.Utils.isValidObject(item.foi)) {
          var offerings = [];
          offerings = offerings.concat(this.sos.getOfferingsForFeatureOfInterestId(item.foi.id));

          for(var i = 0, len = offerings.length; i < len; i++) {
            ids.push(offerings[i].id);

            // Optionally transform from fully-qualified name (FQN) to name
            if(this.config.menu.options.offerings.useFqn) {
              names.push(offerings[i].name);
            } else {
              names.push(SOS.Utils.toTitleCase(SOS.Utils.toDisplayName(SOS.Utils.fqnToName(offerings[i].name))));
            }
          }
        } else {
          ids = this.sos.getOfferingIds();

          // Optionally transform from fully-qualified name (FQN) to name
          if(this.config.menu.options.offerings.useFqn) {
            names = this.sos.getOfferingNames();
          } else {
            names = SOS.Utils.toTitleCase(SOS.Utils.toDisplayName(SOS.Utils.fqnToName(this.sos.getOfferingNames())));
          }
        }

        for(var i = 0, len = ids.length; i < len; i++) {
          var entry = {value: ids[i], label: names[i]};
          this.config.menu.entries.push(entry);
        }
      },

      /**
       * Setup event handlers to manage the offerings menu behaviour
       */
      setupOfferingsBehaviour: function() {
        var m = jQuery('#' + this.config.menu.id);
        var s = jQuery('#' + this.config.menu.id + 'OfferingsTab > .sos-menu-select-list');

        // List observed properties for each selected offering
        s.bind("change", {self: this}, function(evt) {
          var self = evt.data.self;
          var vals = [];

          /* Ensure vals is array, even if listbox is singular
             (otherwise vals.length is the string length of the entry!) */
          vals = vals.concat(jQuery(this).val());

          for(var i = 0, len = vals.length; i < len; i++) {
            var item = {offering: {id: vals[i]}};

            // We can either create a new item per offering, or update existing
            if(self.config.menu.options.createNewItem) {
              self.setNewItem(item);
            } else {
              self.updateCurrentItem(item);
            }
          }
          self.displayObservedProperties();

          // For external listeners (application-level plumbing)
          self.sos.events.triggerEvent("sosMenuOfferingChange");
        });
      },

      /**
       * Construct the offerings menu tab controls
       */
      constructOfferingsTabControls: function(options) {
        var tab = jQuery('#' + this.config.menu.id + 'OfferingsTab');
        var options = options || {};
        var mOpts = this.config.menu.options;

        // Optionally show the "search offerings" input
        if(mOpts.searchOfferings.active) {
          if(options.showPrompt) {
            tab.append('<p/>', mOpts.searchOfferings.prompt);
          }
          tab.append('<p/>', this.constructSearchOfferingsInput());
        }

        // Optionally show the "list all offerings" link
        if(mOpts.listAllOfferings.active) {
          if(options.showPrompt) {
            tab.append('<p/>', mOpts.listAllOfferings.prompt);
          }
          tab.append('<p/>', this.constructListAllOfferingsLink());
        }
      },

      /**
       * Construct an input (& handler) to search offerings
       */
      constructSearchOfferingsInput: function() {
        var item = this.getCurrentItem();

        /* N.B.: A selected FOI acts as a filter on available offerings.  We
                 want to search all offerings, hence the filterOnFOI setting */

        // Clone the offerings entries as source for the search autocomplete
        this.constructOfferingsEntries({filterOnFOI: false});
        var src = this.config.menu.entries.slice(0);

        /* Create an autocomplete search box with placeholder text.  Filter
           offerings based on user selection */
        var c = jQuery('<input/>', {
          "class": "sos-watermark sos-menu-search-box",
          value: this.config.menu.options.searchOfferings.label
        }).autocomplete({source: src});

        c.bind("focus", function(evt) {
          var elem = jQuery(this);
          elem.val("");
          elem.removeClass("sos-watermark");
        });

        c.bind("blur", {self: this}, function(evt) {
          var self = evt.data.self;
          var elem = jQuery(this);
          elem.val(self.config.menu.options.searchOfferings.label);
          elem.addClass("sos-watermark");
        });

        c.bind("autocompleteselect", {self: this}, function(evt, ui) {
          var self = evt.data.self;

          if(SOS.Utils.isValidObject(ui) && SOS.Utils.isValidObject(ui.item)) {
            var tab = jQuery('#' + self.config.menu.id + 'OfferingsTab');
            self.config.menu.selected = [];
            self.config.menu.entries = [];
            self.config.menu.entries.push(ui.item);
            self.initMenu(tab);
            self.setupOfferingsBehaviour();
            self.constructOfferingsTabControls();
            self.promptForSelection(tab);
          }

          return false;
        });

        return c;
      },
 
      /**
       * Construct a link (& handler) to list all offerings
       */
      constructListAllOfferingsLink: function() {
        var l = jQuery('<a/>', {
          text: this.config.menu.options.listAllOfferings.label
        }).button();
        l.bind("click", {self: this}, function(evt) {
          var self = evt.data.self;
          self.config.menu.selected = [];
          self.displayOfferings();
        });

        return l;
      },

      /**
       * Display the observed properties
       */
      displayObservedProperties: function() {
        var tab = jQuery('#' + this.config.menu.id + 'ObservedPropertiesTab');
        this.constructObservedPropertiesEntries();
        this.initMenu(tab);
        this.setupObservedPropertiesBehaviour();
        this.promptForSelection(tab);
      },

      /**
       * Construct the observed properties menu entries
       */
      constructObservedPropertiesEntries: function() {
        var ids = [], names = [];
        this.config.menu.entries = [];
        var item = this.getCurrentItem();

        if(SOS.Utils.isValidObject(item) && SOS.Utils.isValidObject(item.offering)) {
          var offering = this.sos.getOffering(item.offering.id);
          ids = offering.getObservedPropertyIds();

          // Optionally transform from fully-qualified name (FQN) to name
          if(this.config.menu.options.observedProperties.useFqn) {
            names = offering.getObservedPropertyIds();
          } else {
            names = SOS.Utils.toTitleCase(SOS.Utils.toDisplayName(SOS.Utils.fqnToName(offering.getObservedPropertyNames())));
          }

          for(var i = 0, len = ids.length; i < len; i++) {
            var entry = {value: ids[i], label: names[i]};
            this.config.menu.entries.push(entry);
          }
        }
      },

      /**
       * Setup event handlers to manage the observed properties menu behaviour
       */
      setupObservedPropertiesBehaviour: function() {
        var m = jQuery('#' + this.config.menu.id);
        var s = jQuery('#' + this.config.menu.id + 'ObservedPropertiesTab > .sos-menu-select-list');

        // Each selected item contains the offering & observed property
        s.bind("change", {self: this}, function(evt) {
          var self = evt.data.self;
          var vals = [];
          vals = vals.concat(jQuery(this).val());

          for(var i = 0, vlen = vals.length; i < vlen; i++) {
            var item = {observedProperty: vals[i]};
            self.updateCurrentItem(item);
          }
          // For external listeners (application-level plumbing)
          self.sos.events.triggerEvent("sosMenuObservedPropertyChange");
        });
      },

      /**
       * Display the controls
       */
      displayControls: function() {
        var tab = jQuery('#' + this.config.menu.id + 'ControlsTab');
        this.initControls();
        this.promptForSelection(tab);
      },

      /**
       * Initialise the controls
       */
      initControls: function() {
        this.constructControls();
        this.setupControlsBehaviour();
      },

      /**
       * Construct the controls
       */
      constructControls: function() {
        var tab = jQuery('#' + this.config.menu.id + 'ControlsTab');
        var options = this.config.menu.options;

        tab.html("");

        // Plot control section container
        if(options.plotTableControlsSection.active) {
          tab.append(jQuery('<span></span>', {
            "class": "sos-control-title",
            text: options.plotTableControlsSection.sectionLabel
          }));
          var csc1 = jQuery('<div class="sos-control-section sos-control-section-container"/>');
          tab.append(csc1);
          csc1.append(jQuery('<span></span>', {
            text: options.plotTableControlsSection.dateLabel
          }));

          // Start datetime
          var sd = jQuery('<input type="text" id="' + this.config.menu.id + 'ControlsStartDatetime"/>');
          sd.datepicker(this.config.menu.options.datePickers);
          csc1.append('<br/>', sd);

          // End datetime
          var ed = jQuery('<input type="text" id="' + this.config.menu.id + 'ControlsEndDatetime"/>');
          ed.datepicker(this.config.menu.options.datePickers);
          csc1.append('<br/>', ed);

          // Add-to-existing
          var add = jQuery('<input type="checkbox" id="' + this.config.menu.id + 'ControlsAddToExisting"></input>');
          var addLabel = jQuery('<span></span>', {
            text: options.plotTableControlsSection.addToLabel
          });
          csc1.append('<br/>', add, addLabel);

          // Download data
          if(options.plotTableControlsSection.downloadData.active) {
            var dl = jQuery('<span></span>', {
              id: this.config.menu.id + "ControlsDownloadData",
              "class": "sos-menu-icon-button"
            }).button({
              icons: {
                primary: options.plotTableControlsSection.downloadData.icon
              }
            });
            var dlLabel = jQuery('<span></span>', {
              text: options.plotTableControlsSection.downloadData.label
            });
            csc1.append('<br/>', dl, dlLabel);
          }
        }
      },

      /**
       * Setup event handlers to manage the controls behaviour
       */
      setupControlsBehaviour: function() {
        var sd = jQuery('#' + this.config.menu.id + 'ControlsStartDatetime');
        var ed = jQuery('#' + this.config.menu.id + 'ControlsEndDatetime');
        var add = jQuery('#' + this.config.menu.id + 'ControlsAddToExisting');
        var dl = jQuery('#' + this.config.menu.id + 'ControlsDownloadData');

        // Add the start/end date to any selected items
        sd.bind("change", {self: this, pos: "start"}, this.datepickerChangeHandler);
        ed.bind("change", {self: this, pos: "end"}, this.datepickerChangeHandler);
        add.bind("change", {self: this}, this.addToExistingChangeHandler);
        dl.bind("click", {self: this}, this.downloadDataClickHandler);
      },

      /**
       * Set datepicker values from the time properties of the given item
       */
      setDatepickerValues: function(item) {
        if(SOS.Utils.isValidObject(item.time)) {
          if(SOS.Utils.isValidObject(item.time.startDatetime)) {
            var sd = jQuery('#' + this.config.menu.id + 'ControlsStartDatetime');
            sd.datepicker("setDate", SOS.Utils.isoToDateObject(item.time.startDatetime));
          }
          if(SOS.Utils.isValidObject(item.time.endDatetime)) {
            var ed = jQuery('#' + this.config.menu.id + 'ControlsEndDatetime');
            ed.datepicker("setDate", SOS.Utils.isoToDateObject(item.time.endDatetime));
          }
        }
      },

      /**
       * Get datepicker values & store in the time properties of the returned
       * item object
       */
      getDatepickerValues: function() {
        var item = {time: {startDatetime: null, endDatetime: null}};
        var sd = jQuery('#' + this.config.menu.id + 'ControlsStartDatetime');
        var ed = jQuery('#' + this.config.menu.id + 'ControlsEndDatetime');
        var start = sd.datepicker("getDate");
        var end = ed.datepicker("getDate");

        // N.B.: Datepicker returns the client's local time, but we require UTC

        // Ensure the date range is inclusive
        if(start) {
          start.setTime(start.getTime() - start.getTimezoneOffset() * 60 * 1000);
          item.time.startDatetime = start.toISOString();
        }
        if(end) {
          end.setTime(end.getTime() - end.getTimezoneOffset() * 60 * 1000);
          item.time.endDatetime = (new Date(end.getTime() + 8.64e7 - 1)).toISOString();
        }

        return item;
      },

      /**
       * Get current date values & store in the current selected item
       */
      updateCurrentItemDateRange: function() {
        var item = this.getDatepickerValues();

        return this.updateCurrentItem(item);
      },
 
      /**
       * Event handler for datepicker change
       */
      datepickerChangeHandler: function(evt) {
        var self = evt.data.self;
        var pos = evt.data.pos;
        var val = jQuery(this).datepicker("getDate");
        var item = {time: {}};
        var firstItem = self.getFirstItem();

        /* N.B.: The pos property identifies whether this is a start/end date.
                 The time axis is always set the same for all selected items */

        if(pos == "start") {
          if(val) {
            val.setTime(val.getTime() - val.getTimezoneOffset() * 60 * 1000);
            item.time.startDatetime = val.toISOString();
          }

          if(SOS.Utils.isValidObject(firstItem) && SOS.Utils.isValidObject(firstItem.time)) {
            item.time.endDatetime = firstItem.time.endDatetime;
          }
        } else if(pos == "end") {
          // Ensure end datetime is inclusive
          if(val) {
            val.setTime(val.getTime() - val.getTimezoneOffset() * 60 * 1000);
            item.time.endDatetime = (new Date(val.getTime() + 8.64e7 - 1)).toISOString();
          }

          if(SOS.Utils.isValidObject(firstItem) && SOS.Utils.isValidObject(firstItem.time)) {
            item.time.startDatetime = firstItem.time.startDatetime;
          }
        }

        // Add date to any existing selected items, or create if none exist
        self.updateCurrentItem(item);
        self.updateItemsTimeInterval(item);

        // For external listeners (application-level plumbing)
        if(pos == "start") {
          self.sos.events.triggerEvent("sosMenuStartDatetimeChange");
        } else if(pos == "end") {
          self.sos.events.triggerEvent("sosMenuEndDatetimeChange");
        }
      },

      /**
       * Event handler for add-to-existing checkbox change
       */
      addToExistingChangeHandler: function(evt) {
        var self = evt.data.self;
        var flag = jQuery(this).is(':checked');

        // Add flag to current selected item, or create if it doesn't exist
        var item = {options: {addToExisting: flag}};
        self.updateCurrentItem(item);

        // For external listeners (application-level plumbing)
        self.sos.events.triggerEvent("sosMenuAddToExistingChange");
      },

      /**
       * Event handler for download data button click.  This merely passes
       * on the fact that the download data button was clicked, via the
       * sosMenuDownloadDataClick event.  This event should be handled at
       * the application level, to actually download the data
       */
      downloadDataClickHandler: function(evt) {
        var self = evt.data.self;

        // For external listeners (application-level plumbing)
        self.sos.events.triggerEvent("sosMenuDownloadDataClick");
      },

      /**
       * Construct the menu according to this object's properties
       */
      constructMenu: function() {
        var mc1 = jQuery('#' + this.config.menu.id + 'Container');
        var mc2 = jQuery('#' + this.config.menu.id + 'ControlsContainer');
        var m1 = jQuery('#' + this.config.menu.id);
        var m2 = jQuery('#' + this.config.menu.id + 'Controls');

        // If menu container div doesn't exist, create one on the fly
        if(mc1.length < 1) {
          mc1 = jQuery('<div id="' + this.config.menu.id + 'Container" class="sos-menu-container"/>');
          jQuery('body').append(mc1);
        }

        // If menu controls container div doesn't exist, create one on the fly
        if(mc2.length < 1) {
          mc2 = jQuery('<div id="' + this.config.menu.id + 'Container" class="sos-menu-controls-container"/>');
          jQuery('body').append(mc2);
        }

        // If menu div doesn't exist, create one on the fly
        if(m1.length < 1) {
          m1 = jQuery('<div id="' + this.config.menu.id + '" class="sos-menu"/>');
          mc1.append(m1);
        }

        // If menu controls div doesn't exist, create one on the fly
        if(m2.length < 1) {
          m2 = jQuery('<div id="' + this.config.menu.id + 'ControlsPanel" class="sos-menu-controls"/>');
          mc2.append(m2);
        }

        // Construct the menu according to what tabs have been configured
        var tabs = this.constructMenuTabs();

        if(tabs) {
          m1.append(tabs);
        }

        tabs = this.constructMenuControlsTabs();

        if(tabs) {
          m2.append(tabs);
        }

        // Setup menu event handlers
        m1.bind('accordionchange', {self: this}, this.changeMenuTabHandler);
        m2.bind('accordionchange', {self: this}, this.changeMenuTabHandler);

        /* Configure & instantiate the menu.  N.B.: These options do the same
           thing; fillSpace is the older jQuery UI method */
        var opts = {fillSpace: true, heightStyle: "fill"};

        if(this.config.menu.step > -1) {
          opts.active = this.config.menu.step;
        }
        m1.accordion(opts);
        m2.accordion(opts);

        this.config.menu.object = {menu: m1, controls: m2};
      },

      /**
       * Construct menu tabs according to this object's properties
       */
      constructMenuTabs: function() {
        var tabs, text = "";
        var options = this.config.menu.options;

        if(SOS.Utils.isValidObject(options.tabs.offerings) && options.tabs.offerings.active) {
          text += '<h3><a href="#">' + options.tabs.offerings.label + '</a></h3><div id="' + this.config.menu.id + 'OfferingsTab"></div>';
        }
        if(SOS.Utils.isValidObject(options.tabs.observedProperties) && options.tabs.observedProperties.active) {
          text += '<h3><a href="#">' + options.tabs.observedProperties.label + '</a></h3><div id="' + this.config.menu.id + 'ObservedPropertiesTab"></div>';
        }

        tabs = jQuery(text);

        return tabs;
      },

      /**
       * Construct menu controls tabs according to this object's properties
       */
      constructMenuControlsTabs: function() {
        var tabs, text = "";
        var options = this.config.menu.options;

        if(SOS.Utils.isValidObject(options.tabs.controls) && options.tabs.controls.active) {
          text += '<h3><a href="#">' + options.tabs.controls.label + '</a></h3><div id="' + this.config.menu.id + 'ControlsTab"></div>';
        }

        tabs = jQuery(text);

        return tabs;
      },

      /**
       * Initialise menu entries according to this object's properties
       */
      initMenu: function(tab) {
        var lb = this.config.menu.options.listBoxes;
        var s = jQuery('<select id="' + this.config.menu.id + 'SelectList"' + (lb.multiple ? ' multiple="multiple"' : '') + (lb.size ? ' size="' + lb.size + '"' : '') + ' class="sos-menu-select-list"></select>');

        tab.html("");
        tab.append(s);

        // Initialise the menu entries
        for(var i = 0, len = this.config.menu.entries.length; i < len; i++) {
          var opt = jQuery("<option></option>", {
            value: this.config.menu.entries[i].value,
            html: this.config.menu.entries[i].label
          });
          if(lb.useToolTip) {
            opt.attr("title", this.config.menu.entries[i].label);
          }
          s.append(opt);
        }

        if(lb.useSelectBox && typeof jQuery('body').selectBox == "function") {
          // This call uses a jquery plugin to replace vanilla select boxes
          jQuery('.sos-menu-select-list').selectBox();
        }
      },

      /**
       * Initialise an initial blank menu according to this object's properties
       */
      initBlankMenu: function() {
        var options = this.config.menu.options;

        if(SOS.Utils.isValidObject(options.tabs.offerings) && options.tabs.offerings.active) {
          var t = jQuery('#' + this.config.menu.id + 'OfferingsTab');

          if(typeof t.html() == "undefined" || jQuery.trim(t.html()) == "") {
            t.html(options.tabs.offerings.prompt);
            this.constructOfferingsTabControls({showPrompt: true});
          }
        }
        if(SOS.Utils.isValidObject(options.tabs.observedProperties) && options.tabs.observedProperties.active) {
          var t = jQuery('#' + this.config.menu.id + 'ObservedPropertiesTab');

          if(typeof t.html() == "undefined" || jQuery.trim(t.html()) == "") {
            t.html(options.tabs.observedProperties.prompt);
          }
        }
        if(SOS.Utils.isValidObject(options.tabs.controls) && options.tabs.controls.active) {
          var t = jQuery('#' + this.config.menu.id + 'ControlsTab');

          if(typeof t.html() == "undefined" || jQuery.trim(t.html()) == "") {
            t.html(options.tabs.controls.prompt);
          }
        }
      },

      /**
       * Setup behaviour for when user moves between menu tabs
       */
      changeMenuTabHandler: function(evt, ui) {
        var self = evt.data.self;
        var options = self.config.menu.options;

        if(SOS.Utils.isValidObject(options.tabs.offerings) && options.tabs.offerings.active) {
          if(ui.newHeader.text() == options.tabs.offerings.label) {
            var t = jQuery('#' + self.config.menu.id + 'OfferingsTab');

            if(typeof t.html() == "undefined" || jQuery.trim(t.html()) == "") {
              t.html(options.tabs.offerings.prompt);
              self.constructOfferingsTabControls({showPrompt: true});
            }
          }
        }
        if(SOS.Utils.isValidObject(options.tabs.observedProperties) && options.tabs.observedProperties.active) {
          if(ui.newHeader.text() == options.tabs.observedProperties.label) {
            var t = jQuery('#' + self.config.menu.id + 'ObservedPropertiesTab');

            if(typeof t.html() == "undefined" || jQuery.trim(t.html()) == "") {
              t.html(options.tabs.observedProperties.prompt);
            }
          }
        }
        if(SOS.Utils.isValidObject(options.tabs.controls) && options.tabs.controls.active) {
          if(ui.newHeader.text() == options.tabs.controls.label) {
            var t = jQuery('#' + self.config.menu.id + 'ControlsTab');

            if(typeof t.html() == "undefined" || jQuery.trim(t.html()) == "") {
              t.html(options.tabs.controls.prompt);
            }
          }
        }
      },

      /**
       * Optionally auto-select the given tab as a prompt for selection
       */
      promptForSelection: function(tab) {
        if(this.config.menu.options.promptForSelection) {
          tab.prev('h3[role="tab"]').trigger('click');
        }
      },

      /**
       * Create a new selected item.  The index is returned
       */
      setNewItem: function(item) {
        this.config.menu.selected = this.config.menu.selected || [];
        this.config.menu.selected.push({item: item});

        return (this.config.menu.selected.length - 1);
      },

      /**
       * Get the selected item corresponding to the given index.  If the
       * index is < 0, then it is an offset from the end of the array.  The
       * item is returned or undefined if no such item exists
       */
      getItem: function(index) {
        var item;

        if(SOS.Utils.isValidObject(this.config.menu.selected)) {
          var len = this.config.menu.selected.length || 0;

          if(len > 0) {
            if(index < 0) {
              index = len + index;
            }
            if(index >= 0 && index < len) {
              item = this.config.menu.selected[index].item;
            }
          }
        }

        return item;
      },

      /**
       * Set the selected item corresponding to the given index.  If the
       * index is < 0, then it is an offset from the end of the array.  The
       * (actual, non-negative) index is returned or -1 if no such item exists
       */
      setItem: function(item, index) {
        var len = 0, retval = -1;

        if(SOS.Utils.isValidObject(this.config.menu.selected)) {
          len = this.config.menu.selected.length;
        }
        if(len > 0) {
          if(index < 0) {
            index = len + index;
          }
          if(index >= 0 && index < len) {
            this.config.menu.selected[index].item = item;
            retval = index;
          }
        }

        return retval;
      },

      /**
       * Get the current selected item
       */
      getCurrentItem: function() {
        return this.getItem(-1);
      },

      /**
       * Set the current selected item or create new if no current item exists
       */
      setCurrentItem: function(item) {
        var index = this.setItem(item, -1);

        if(index < 0) {
          index = this.setNewItem(item);
        }

        return index;
      },

      /**
       * Update given properties of the current selected item.  If there is
       * no current item, then one is created
       */
      updateCurrentItem: function(properties) {
        var item = this.getCurrentItem();

        if(SOS.Utils.isValidObject(item)) {
          jQuery.extend(true, item, properties);
          this.setCurrentItem(item);
        } else {
          this.setNewItem(properties);
        }

        return item;
      },

      /**
       * Get the first selected item
       */
      getFirstItem: function() {
        return this.getItem(0);
      },

      /**
       * Update any existing selected items with the given item's time interval
       */
      updateItemsTimeInterval: function(item) {
        if(SOS.Utils.isValidObject(item.time)) {
          if(SOS.Utils.isValidObject(this.config.menu.selected)) {
            var len = this.config.menu.selected.length;

            // Update time interval for any existing selected items
            for(var i = 0; i < len; i++) {
              if(SOS.Utils.isValidObject(this.config.menu.selected[i].item)) {
                this.config.menu.selected[i].item.time = this.config.menu.selected[i].item.time || {};

                if(SOS.Utils.isValidObject(item.time.startDatetime)) {
                  this.config.menu.selected[i].item.time.startDatetime = item.time.startDatetime;
                }
                if(SOS.Utils.isValidObject(item.time.endDatetime)) {
                  this.config.menu.selected[i].item.time.endDatetime = item.time.endDatetime;
                }
              }
            }
          }
        }
      }
    });
  }

  /* Create the SOS.Info namespace */
  if(typeof SOS.Info === "undefined") {
    /**
     * SOS.Info Class
     * Class for displaying supplementary information in a SOS application
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.Info = OpenLayers.Class(SOS.Ui, {
      url: null,
      sos: null,
      offering: null,
      observedProperty: null,
      config: null,
      CLASS_NAME: "SOS.Info",

      /**
       * Constructor for a SOS.Info object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.offering = null;
        this.observedProperty = null;
        this.config = {
          info: {
            object: null,
            id: "sosInfo",
            "class": "sos-info-box",
            eventHandlers: [],
            content: null,
            contentTemplate: null,
            options: {
              show: true,
              makeDraggable: true,
              initialContent: {
                active: true
              },
              contentSection: {
                active: true,
                "class": "sos-info-box-content"
              },
              controlsSection: {
                active: true,
                "class": "sos-info-box-control-section"
              },
              controlsSectionTitle: {
                active: true,
                "class": "sos-info-box-control-section-title",
                label: ""
              },
              showHideControl: {
                active: true,
                "class": "sos-info-box-control ui-icon",
                icons: {
                  show: "ui-icon-triangle-1-s",
                  hide: "ui-icon-triangle-1-e"
                }
              },
              closeControl: {
                active: true,
                "class": "sos-info-box-control-right ui-icon",
                icons: {
                  close: "ui-icon-close"
                }
              }
            }
          },
          format: {
            time: {
              formatter: SOS.Utils.jsTimestampToIso
            },
            value: {
              sciLimit: 0.1,
              digits: 2,
              formatter: SOS.Ui.prototype.formatValueFancy
            }
          }
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.Info object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the info object
       */
      setInfoOptions: function(options) {
        jQuery.extend(true, this.config.info.options, options);
      },

      /**
       * Set the content for the info panel
       */
      setContent: function(content) {
        this.config.info.content = content;
      },

      /**
       * Get the content for the info panel
       */
      getContent: function() {
        return this.config.info.content;
      },

      /**
       * Append the given content to any existing content for the info panel
       */
      appendToContent: function(content) {
        if(this.config.info.content) {
          this.config.info.content += content;
        } else {
          this.config.info.content = content;
        }
      },

      /**
       * Set the content template for the info panel
       */
      setContentTemplate: function(contentTemplate) {
        this.config.info.contentTemplate = contentTemplate;
      },

      /**
       * Get the content template for the info panel
       */
      getContentTemplate: function() {
        return this.config.info.contentTemplate;
      },

      /**
       * Append the given content template to any existing content template
       * for the info panel
       */
      appendToContentTemplate: function(contentTemplate) {
        if(this.config.info.contentTemplate) {
          this.config.info.contentTemplate += contentTemplate;
        } else {
          this.config.info.contentTemplate = contentTemplate;
        }
      },
 
      /**
       * Initialise the content for the info panel as the content template
       */
      initContentFromTemplate: function() {
        if(this.config.info.contentTemplate) {
          this.setContent(this.config.info.contentTemplate);
        }
      },
 
      /**
       * Substitute matches of the given regexp with the given content against
       * the content template, and store in the content for this info panel
       */
      setContentFromTemplate: function(regexp, content) {
        if(this.config.info.content) {
          this.setContent(this.config.info.content.replace(regexp, content));
        }
      },

      /**
       * Set the title for the info panel
       */
      setTitle: function(title) {
        this.setInfoOptions({controlsSectionTitle: {label: title}});
      },

      /**
       * Set CSS class for the info panel
       */
      setClass: function(c) {
        this.config.info["class"] = c;
      },

      /**
       * Add a CSS class to the info panel
       */
      addClass: function(c) {
        this.config.info["class"] += " " + c;
      },

      /**
       * Add an event handler object to the info panel's event handler array.
       * The event handler object has the form:
       * {event: e, scope: object, callback: function}
       * The callback function will be called in the given scope
       */
      addEventHandler: function(h) {
        this.config.info.eventHandlers.push(h);
      },
   
      /**
       * Generate the info panel
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          jQuery.extend(true, this.config.info.options, options);
        }
        if(this.config.info.options.show) {
          this.initInfoPanel();
          this.displayInitialContent();
        }
      },
 
      /**
       * Initialise the info panel
       */
      initInfoPanel: function() {
        var p = jQuery('#' + this.config.info.id);

        // If info panel div doesn't exist, create one on the fly
        if(p.length < 1) {
          p = jQuery("<div></div>", {
            id: this.config.info.id,
            "class": this.config.info["class"]
          });
          jQuery('body').append(p);
        }
        this.config.info.object = p;

        // Setup the info panel & its controls
        if(this.config.info.options.makeDraggable) {
          this.config.info.object.draggable();
        }
        this.addContentSection();
        this.addControlsSection();
        this.addControlsSectionTitle();
        this.addShowHideControl();
        this.addCloseControl();
        this.setupBehaviour();
      },
 
      /**
       * Display the initial content for this info panel
       */
      displayInitialContent: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          if(this.config.info.options.show && this.config.info.options.initialContent.active) {
            this.displayContent();
          }
        }
      },
 
      /**
       * Display the previously set content for this info panel
       */
      displayContent: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          var s = this.config.info.object.children("." + this.config.info.options.contentSection["class"]);
          s.html(this.config.info.content);
        }
      },
 
      /**
       * Set the content for this info panel and then display it
       */
      updateContent: function(content) {
        this.setContent(content);
        this.displayContent();
      },
 
      /**
       * Substitute matches of the given regexp with the given content against
       * the content template, and store in the content for this info panel,
       * then display it
       */
      updateContentFromTemplate: function(regexp, content) {
        if(this.config.info.content) {
          this.setContent(this.config.info.content.replace(regexp, content));
          this.displayContent();
        }
      },

      /**
       * Adds a content section to this info panel.  This holds the content
       * of the info panel
       */
      addContentSection: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          if(this.config.info.options.show && this.config.info.options.contentSection.active) {
            var c = jQuery("<div></div>", {
              "class": this.config.info.options.contentSection["class"]
            });

            // Add the content section to this info panel
            this.config.info.object.append(c);
          }
        }
      },

      /**
       * Adds a controls section to this info panel.  This groups all controls
       * of the info panel
       */
      addControlsSection: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          if(this.config.info.options.show && this.config.info.options.controlsSection.active) {
            var c = jQuery("<div></div>", {
              "class": this.config.info.options.controlsSection["class"]
            });

            // Add the controls section to this info panel
            this.config.info.object.append(c);
          }
        }
      },

      /**
       * Adds a controls section title to this info panel
       */
      addControlsSectionTitle: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          if(this.config.info.options.show && this.config.info.options.controlsSectionTitle.active) {
            var c = jQuery("<div></div>", {
              "class": this.config.info.options.controlsSectionTitle["class"],
              html: this.config.info.options.controlsSectionTitle.label
            });

            // Add the control to this info panel's control section
            var s = this.config.info.object.children("." + this.config.info.options.controlsSection["class"]);
            s.append(c);
          }
        }
      },

      /**
       * Adds a show/hide control to this info panel.  This control toggles the
       * visibility of the content of the info panel
       */
      addShowHideControl: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          if(this.config.info.options.show && this.config.info.options.showHideControl.active) {
            var c = jQuery("<div></div>", {
              "class": this.config.info.options.showHideControl["class"]
            });
            c.addClass(this.config.info.options.showHideControl.icons.show);
            c.bind("click", {self: this}, this.showHideControlClickHandler);

            // Add the control to this info panel's control section
            var s = this.config.info.object.children("." + this.config.info.options.controlsSection["class"]);
            s.append(c);
          }
        }
      },

      /**
       * Event handler for show/hide control click.  This toggles the
       * visibility of the content of the info panel, changing the icon
       * accordingly
       */
      showHideControlClickHandler: function(evt) {
        var c = jQuery(this);
        var icons = evt.data.self.config.info.options.showHideControl.icons;

        if(c.hasClass(icons.show)) {
          c.parent().siblings().hide();
          c.removeClass(icons.show);
          c.addClass(icons.hide);
        } else {
          c.parent().siblings().show();
          c.removeClass(icons.hide);
          c.addClass(icons.show);
        }
      },

      /**
       * Adds a close control to this info panel.  This control closes the
       * info panel (i.e. removes it from the DOM)
       */
      addCloseControl: function() {
        if(SOS.Utils.isValidObject(this.config.info.object)) {
          if(this.config.info.options.show && this.config.info.options.closeControl.active) {
            var c = jQuery("<div></div>", {
              "class": this.config.info.options.closeControl["class"]
            });
            c.addClass(this.config.info.options.closeControl.icons.close);
            c.bind("click", {self: this}, this.closeControlClickHandler);

            // Add the control to this info panel's control section
            var s = this.config.info.object.children("." + this.config.info.options.controlsSection["class"]);
            s.append(c);
          }
        }
      },

      /**
       * Event handler for close control click.  This closes the
       * info panel (i.e. removes it from the DOM)
       */
      closeControlClickHandler: function(evt) {
        var p = evt.data.self.config.info.object;

        if(p) {
          p.remove();
        }
      },

      /**
       * Register all configured event handlers, to manage the info panel's
       * runtime behaviour
       */
      setupBehaviour: function() {
        if(SOS.Utils.isValidObject(this.sos)) {
          for(var i = 0, len = this.config.info.eventHandlers.length; i < len; i++) {
            var h = this.config.info.eventHandlers[i];

            if(SOS.Utils.isValidObject(h) && SOS.Utils.isValidObject(h.event) && SOS.Utils.isValidObject(h.callback)) {
              h.scope = h.scope || this;
              this.sos.registerUserCallback(h);
            }
          }
        }
      }
    });
  }

  /* Create the SOS.App namespace */
  if(typeof SOS.App === "undefined") {
    /**
     * SOS.App Class
     * Application class for pulling all the SOS.Ui components together
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.App = OpenLayers.Class(SOS.Ui, {
      url: null,
      sos: null,
      offering: null,
      offeringId: null,
      observedProperty: null,
      foiId: null,
      startDatetime: null,
      endDatetime: null,
      relativeTime: null,
      config: null,
      CLASS_NAME: "SOS.App",

      /**
       * Constructor for a SOS.App object
       *
       * @constructor
       */
      initialize: function(options) {
        this.url = null;
        this.sos = null;
        this.offering = null;
        this.offeringId = null;
        this.observedProperty = null;
        this.foiId = null;
        this.startDatetime = null;
        this.endDatetime = null;
        this.relativeTime = null;
        this.config = {
          app: {
            object: null,
            id: "sosApp",
            components: {
              menu: null,
              map: null,
              plot: null,
              table: null,
              infoMetadata: null,
              infoHelp: null
            },
            options: {
              tabs: {
                map: {label: "Map"},
                plot: {label: "Plot"},
                table: {label: "Table"}
              },
              time: {
                useOfferingTimePeriod: false,
                ms: 31 * 8.64e7
              },
              foi: {
                getTemporalCoverage: true
              },
              observation: {
                useFoiId: true
              },
              /* Any properties set on sub-objects of this, will be passed
                 down to the corresponding component on instantiation.
                 For example:
                 components: {
                   plot: {
                     plot: {options: {zoom: {interactive: false}}},
                     format: {value: {digits: 1}}
                   }
                 }
              */
              components: {
                table: {table: {options: {scrollable: true}}},
                menu: {menu: {step: -1}}
              },
              info: SOS.App.Resources.config.app.options.info
            }
          },
          overview: {
            object: null,
            id: "sosAppOverview",
            series: [],
            options: {
              show: true,
              xaxis: {ticks: [], mode: "time"},
              yaxis: {ticks: [], autoscaleMargin: 0.1},
              selection: {mode: "x"},
              grid: {borderWidth: 1},
              legend: {show: false},
              series: {lines: {show: true, lineWidth: 1}, shadowSize: 0}
            }
          },
          format: {
            time: {
              formatter: SOS.Utils.jsTimestampToIso
            },
            value: {
              sciLimit: 0.1,
              digits: 2,
              formatter: SOS.Ui.prototype.formatValueFancy
            }
          }
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Destructor for a SOS.App object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the app
       */
      setAppOptions: function(options) {
        jQuery.extend(true, this.config.app.options, options);
      },

      /**
       * Set options for the app overview
       */
      setOverviewOptions: function(options) {
        jQuery.extend(true, this.config.overview.options, options);
      },

      /**
       * Set options for the app components
       */
      setAppComponentsOptions: function(options) {
        jQuery.extend(true, this.config.app.options.components, options);
      },

      /**
       * Generate the app using this object's properties to query the SOS
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          jQuery.extend(true, this, options);
        }

        if(!this.haveValidCapabilitiesObject()) {
          this.getCapabilities(this._display);
        } else {
          this._display();
        }
      },

      /**
       * Get data from the SOS according to this object's properties, & then
       * draw the app
       */
      _display: function() {
        this.setupPlumbing();
        this.displayApp();
      },
 
      /**
       * Setup the plumbing between this app's components
       */
      setupPlumbing: function() {
        this.initComponents();
        this.setupComponentsBehaviour();
      },
 
      /**
       * Initialise this app's components
       */
      initComponents: function() {
        var components = this.config.app.components;
        var options = {
          url: this.url,
          sos: this.sos
        };

        // Instantiate the components of the app with common options
        components.menu = new SOS.Menu(options);
        components.map = new SOS.Map(options);
        components.plot = new SOS.Plot(options);
        components.table = new SOS.Table(options);
        components.infoMetadata = new SOS.Info(options);
        components.infoHelp = new SOS.Info(options);

        // Ensure that all components share common time/value formatters
        for(var p in components) {
          components[p].config.format = this.config.format;
        }

        // Set the IDs of where each component is located on the page
        components.menu.config.menu.id = this.config.app.id + "Menu";
        components.map.config.map.id = this.config.app.id + "MapPanel";
        components.plot.config.plot.id = this.config.app.id + "PlotPanel";
        components.table.config.table.id = this.config.app.id + "TablePanel";
        components.infoMetadata.config.info.id = this.config.app.id + "InfoMetadataBox";
        components.infoHelp.config.info.id = this.config.app.id + "InfoHelpBox";

        // Set any component-specific initial options
        this.applyComponentsOptions();

        /* Optionally show data overview.  Using an application-level overview
           allows the selections made on the components (plot, table) to talk
           to one another.  See drawOverview() etc. */
        if(this.config.overview.options.show) {
          components.map.setOverviewOptions({show: true});
          components.plot.config.overview.id = this.config.app.id + "Overview";
          components.table.config.overview.id = this.config.app.id + "Overview";
        }
        components.infoMetadata.addClass("sos-info-metadata-box");
        components.infoHelp.addClass("sos-info-help-box");
        components.infoMetadata.setTitle(this.config.app.options.info.metadata.title);
        components.infoHelp.setTitle(this.config.app.options.info.help.title);
        components.infoMetadata.setContent(this.config.app.options.info.metadata.initialContent);
        components.infoHelp.setContent(this.config.app.options.info.help.initialContent);

        // For external listeners (application-level plumbing)
        this.sos.events.triggerEvent("sosAppInitComponents");
      },
  
      /**
       * Apply any options that have been set for this app's components
       */
      applyComponentsOptions: function() {
        var components = this.config.app.components;
        var confs = this.config.app.options.components;

        /* If a component configuration object exists with the same name as a
           component of this app, then set the configured properties on the
           component */
        if(confs) {
          for(var p in components) {
            if(SOS.Utils.isValidObject(confs[p])) {
              if(SOS.Utils.isValidObject(components[p])) {
                jQuery.extend(true, components[p].config, confs[p]);
              }
            }
          }
        }
      },

      /**
       * Setup the behaviour for this app's components
       */
      setupComponentsBehaviour: function() {
        // Register event handlers to tie the components together
        this.sos.registerUserCallback({event: "sosMapFeatureOfInterestSelect", scope: this, callback: this.sosMapFeatureOfInterestSelectHandler});

        this.sos.registerUserCallback({event: "sosMenuOfferingChange", scope: this, callback: this.sosMenuChangeHandler});

        this.sos.registerUserCallback({event: "sosMenuObservedPropertyChange", scope: this, callback: this.sosMenuChangeHandler});

        this.sos.registerUserCallback({event: "sosMenuStartDatetimeChange", scope: this, callback: this.sosMenuChangeHandler});

        this.sos.registerUserCallback({event: "sosMenuEndDatetimeChange", scope: this, callback: this.sosMenuChangeHandler});

        this.sos.registerUserCallback({event: "sosMenuDownloadDataClick", scope: this, callback: this.sosMenuDownloadDataClickHandler});

        this.setupInfoComponentsBehaviour();

        // For external listeners (application-level plumbing)
        this.sos.events.triggerEvent("sosAppSetupComponentsBehaviour");
      },
 
      /**
       * Setup the behaviour for this app's info components
       */
      setupInfoComponentsBehaviour: function() {
        var components = this.config.app.components;
        var self = this;

        /* Register event handlers to update the application info boxes as the
           user navigates the application.  The scope for each callback is set
           to the particular info box, so we use 'self' to refer to this app */
        this.sos.registerUserCallback({
          event: "sosCapsAvailable",
          scope: components.infoMetadata,
          callback: function() {
            var ft = this.config.format.time;
            var c = this.sos.SOSCapabilities.serviceIdentification.title;
            if(SOS.Utils.isValidObject(this.sos.SOSCapabilities.operationsMetadata.GetObservation.parameters.eventTime)) {
              c += "<p/>Data Availability<br/>"
              + "Starts: " + ft.formatter(this.sos.SOSCapabilities.operationsMetadata.GetObservation.parameters.eventTime.allowedValues.range.minValue) + "<br/>"
              + "Ends: " + ft.formatter(this.sos.SOSCapabilities.operationsMetadata.GetObservation.parameters.eventTime.allowedValues.range.maxValue) + "<br/>";
            }
            this.updateContent(c);
          }
        });

        this.sos.registerUserCallback({
          event: "sosCapsAlreadyAvailable",
          scope: components.infoMetadata,
          callback: function() {
            var ft = this.config.format.time;
            var c = this.sos.SOSCapabilities.serviceIdentification.title;
            if(SOS.Utils.isValidObject(this.sos.SOSCapabilities.operationsMetadata.GetObservation.parameters.eventTime)) {
              c += "<p/>Data Availability<br/>"
              + "Starts: " + ft.formatter(this.sos.SOSCapabilities.operationsMetadata.GetObservation.parameters.eventTime.allowedValues.range.minValue) + "<br/>"
              + "Ends: " + ft.formatter(this.sos.SOSCapabilities.operationsMetadata.GetObservation.parameters.eventTime.allowedValues.range.maxValue) + "<br/>";
            }
            this.updateContent(c);
          }
        });

        /* The sosCapsAvailable event will probably have already fired by the
           time we're setting up these handlers, hence the custom event
           sosCapsAlreadyAvailable, so we can update the metadata info panel */
        if(components.infoMetadata.haveValidCapabilitiesObject()) {
          this.sos.events.triggerEvent("sosCapsAlreadyAvailable");
        }

        this.sos.registerUserCallback({
          event: "sosMenuOfferingChange",
          scope: components.infoMetadata,
          callback: function() {
            var ft = this.config.format.time;
            var item = components.menu.getCurrentItem();
            if(item) {
              var off = this.sos.getOffering(item.offering.id);
              var c = off.name
              + "<p/>Data Availability<br/>"
              + "Starts: " + ft.formatter(off.time.timePeriod.beginPosition) + "<br/>"
              + "Ends: " + ft.formatter(off.time.timePeriod.endPosition) + "<br/>";
              this.updateContent(c);
            }
          }
        });

        this.sos.registerUserCallback({
          event: "sosMapFeatureOfInterestSelect",
          scope: components.infoMetadata,
          callback: function() {
            // Setup a template as the content comes from more than one SOS call
            this.setContentTemplate("[%foi%] (lon [%lon%]&deg;, lat [%lat%]&deg;)<p/>Data Availability<br/>Starts: [%startDatetime%]<br/>Ends: [%endDatetime%]<br/>");
            this.initContentFromTemplate();
            var item = components.menu.getCurrentItem();
            if(item) {
              this.setContentFromTemplate(/\[%foi%\]/, item.foi.name);
              var point = self.pointToLonLat(item.foi.geometry);
              this.setContentFromTemplate(/\[%lon%\]/, parseFloat(point.x).toFixed(2));
              this.setContentFromTemplate(/\[%lat%\]/, parseFloat(point.y).toFixed(2));
              // Get data availability over all offerings at this FOI
              if(self.config.app.options.foi.getTemporalCoverage) {
                this.sos.getTemporalCoverageForFeatureOfInterestId(item.foi.id);
              }
            }
          }
        });

        this.sos.registerUserCallback({
          event: "sosTemporalCoverageAvailable",
          scope: components.infoMetadata,
          callback: function() {
            var ft = this.config.format.time;
            if(SOS.Utils.isValidObject(this.sos.SOSTemporalCoverage) && SOS.Utils.isValidObject(this.sos.SOSTemporalCoverage.timePeriod)) {
              this.setContentFromTemplate(/\[%startDatetime%\]/, ft.formatter(this.sos.SOSTemporalCoverage.timePeriod.beginPosition));
              this.setContentFromTemplate(/\[%endDatetime%\]/, ft.formatter(this.sos.SOSTemporalCoverage.timePeriod.endPosition));
              this.displayContent();
            }
          }
        });

        this.sos.registerUserCallback({
          event: "sosAppChangeAppTab",
          scope: components.infoHelp,
          callback: function(evt) {
            var contentName = evt.data.componentName + "Content";
            this.updateContent(self.config.app.options.info.help[contentName]);
          }
        });
      },

      /**
       * Plot the given observation data as an overview plot
       */
      drawOverview: function() {
        var components = this.config.app.components;
        var o = jQuery('#' + this.config.overview.id);

        // If overview div doesn't exist (the norm), create one on the fly
        if(o.length < 1) {
          var a = jQuery('#' + this.config.app.id + 'Container');
          o = jQuery('<div id="sosAppOverview" class="sos-plot-overview"/>');
          a.after(o);
        }

        this.config.overview.series = (components.plot.config.plot.series.length > 0 ? components.plot.config.plot.series : components.table.config.table.series);
        this.config.overview.object = jQuery.plot(o, this.config.overview.series, this.config.overview.options);
      },

      /**
       * Setup event handlers to manage the app overview's behaviour
       */
      setupOverviewBehaviour: function() {
        var components = this.config.app.components;
        var p = jQuery('#' + components.plot.config.plot.id);
        var t = jQuery('#' + components.table.config.table.id);
        var o = jQuery('#' + this.config.overview.id);
        var overview = this.config.overview;

        // These handlers connect the overview, the plot, & the table

        // Subset the plot & table from plot selection.  Overview can reinstate
        p.bind("plotselected", {self: this}, function(evt, ranges) {
          var components = evt.data.self.config.app.components;
          var plotOpts = components.plot.config.plot.object.getOptions();
          jQuery.extend(plotOpts.xaxes[0], {min: ranges.xaxis.from, max: ranges.xaxis.to});
          components.plot.update();
          components.plot.config.plot.object.clearSelection(ranges);

          // Pass on plot selection to table
          components.table.subset(ranges.xaxis.from, ranges.xaxis.to);

          // Don't fire event on the overview to prevent eternal loop
          overview.object.setSelection(ranges, true);
        });
 
        // Drag selection handlers for table
        t.delegate("td", "tableselecting", {self: this}, function(evt) {
          var components = evt.data.self.config.app.components;
          components.table.config.table.selecting = true;
          components.table.highlightSelectedCellGroup(evt.target);
        });

        t.delegate("td", "tableshiftclickselecting", {self: this}, function(evt) {
          var components = evt.data.self.config.app.components;
          components.table.highlightSelectedCellGroup(evt.target);
        });

        // Subset the table & plot from table selection.  Overview can reinstate
        t.delegate("td", "tableselected", {self: this}, function(evt, selection) {
          var components = evt.data.self.config.app.components;
          delete components.table.config.table.selecting;

          if(selection) {
            var ranges = components.table.selectionToRanges(selection);

            if(selection.items.length > 1) {
              components.table.subset(ranges.xaxis.from, ranges.xaxis.to);
              overview.object.setSelection(ranges, true);
              components.table.clearSelectionHighlighting();

              // Pass on table selection to plot
              var e = jQuery.Event('plotselected');
              e.data = {self: evt.data.self};
              p.trigger(e, [ranges]);
            }
          }
        });

        o.bind("plotselected", {self: this}, function(evt, ranges) {
          var components = evt.data.self.config.app.components;
          components.plot.config.plot.object.setSelection(ranges);
        });

        o.bind("plotunselected", {self: this}, function(evt) {
          var components = evt.data.self.config.app.components;
          var plotOpts = components.plot.config.plot.object.getOptions();
          jQuery.extend(plotOpts.xaxes[0], {min: null, max: null});
          components.plot.update();

          components.table.update();
        });
      },

      /**
       * Display the app according to this object's properties
       */
      displayApp: function() {
        var ac = jQuery('#' + this.config.app.id + 'Container');
        var amc = jQuery('#' + this.config.app.id + 'MenuContainer');
        var am = jQuery('#' + this.config.app.id + 'Menu');
        var a = jQuery('#' + this.config.app.id);
        var irc = jQuery('#' + this.config.app.id + 'InfoRightContainer');

        // If app container div doesn't exist, create one on the fly
        if(ac.length < 1) {
          ac = jQuery('<div id="' + this.config.app.id + 'Container" class="sos-app-container"/>');
          jQuery('body').append(ac);
        }

        // If app menu container div doesn't exist, create one on the fly
        if(amc.length < 1) {
          amc = jQuery('<div id="' + this.config.app.id + 'MenuContainer" class="sos-menu-container"/>');
          ac.append(amc);
        }

        // If app menu div doesn't exist, create one on the fly
        if(am.length < 1) {
          am = jQuery('<div id="' + this.config.app.id + 'Menu" class="sos-menu"/>');
          amc.append(am);
        }

        // If app div doesn't exist, create one on the fly
        if(a.length < 1) {
          a = jQuery('<div id="' + this.config.app.id + '" class="sos-app"/>');
          ac.append(a);
        }

        // If app info right container div doesn't exist, create one on the fly
        if(irc.length < 1) {
          irc = jQuery('<div id="' + this.config.app.id + 'InfoRightContainer" class="sos-info-right-container"/>');
          ac.append(irc);
        }

        // Construct the app menu
        this.config.app.components.menu.display();

        // Construct the app according to what tabs have been configured
        var tabs = this.constructAppTabs(a);

        // Setup app tabs event handlers
        a.bind("tabscreate", {self: this}, this.initAppHandler);
        a.tabs();

        // Initially set app menu date range to the default for this app
        this.initMenuDateRangeControls();

        // Display the app info panels & add to the right-hand container
        this.config.app.components.infoMetadata.display();
        this.config.app.components.infoHelp.display();

        var im = jQuery('#' + this.config.app.id + 'InfoMetadataBox');
        var ih = jQuery('#' + this.config.app.id + 'InfoHelpBox');
        irc.append(im, ih);

        this.config.app.object = a;
      },
 
      /**
       * Construct app tabs according to this object's properties
       */
      constructAppTabs: function(container) {
        var tabs, div, divId, a, item;
        var options = this.config.app.options;

        tabs = jQuery('<ul/>');
        container.append(tabs);

        if(SOS.Utils.isValidObject(options.tabs.map)) {
          divId = this.config.app.id + "MapPanel";
          div = jQuery('<div id="' + divId + '" class="sos-map"/>');
          container.append(div);
          a = jQuery('<a href="#' + divId + '"><span class="sos-tab-header">' + options.tabs.map.label + '</span></a>');
          a.bind('click', {self: this, componentName: "map"}, this.changeAppTabHandler);
          item = jQuery('<li id="' + this.config.app.id + 'MapTab"></li>');
          item.append(a);
          tabs.append(item);

          // If we have a map panel, initialise it here
          this.initMap();
        }
        if(SOS.Utils.isValidObject(options.tabs.plot)) {
          divId = this.config.app.id + "PlotPanel";
          div = jQuery('<div id="' + divId + '" class="sos-plot"/>');
          container.append(div);
          a = jQuery('<a href="#' + divId + '"><span class="sos-tab-header">' + options.tabs.plot.label + '</span></a>');
          a.bind('click', {self: this, componentName: "plot"}, this.changeAppTabHandler);
          item = jQuery('<li id="' + this.config.app.id + 'PlotTab"></li>');
          item.append(a);
          tabs.append(item);
        }
        if(SOS.Utils.isValidObject(options.tabs.table)) {
          divId = this.config.app.id + "TablePanel";
          div = jQuery('<div id="' + divId + '" class="sos-table"/>');
          container.append(div);
          a = jQuery('<a href="#' + divId + '"><span class="sos-tab-header">' + options.tabs.table.label + '</span></a>');
          a.bind('click', {self: this, componentName: "table"}, this.changeAppTabHandler);
          item = jQuery('<li id="' + this.config.app.id + 'TableTab"></li>');
          item.append(a);
          tabs.append(item);
        }

        // Optionally show a data overview (for plot, table etc.)
        if(this.config.overview.options.show) {
          divId = this.config.app.id + "Overview";
          div = jQuery('<div id="' + divId + '" class="sos-plot-overview"/>');
          container.append(div);
        }

        return tabs;
      },

      /**
       * Initialise app tabs according to this object's properties
       */
      initAppHandler: function(evt) {
        var self = evt.data.self;
      },

      /**
       * Setup behaviour for when user moves between app tabs
       */
      changeAppTabHandler: function(evt) {
        var self = evt.data.self;
        var components = self.config.app.components;
        var o = jQuery('#' + self.config.app.id + 'Overview');

        // Only show overview plot on pertinent component panels
        if(o.length > 0) {
          evt.data.componentName == "map" ? o.hide() : o.show();
        }

        // For external listeners (application-level plumbing)
        self.sos.events.triggerEvent("sosAppChangeAppTab", evt);
      },
 
      /**
       * Initialise the app map
       */
      initMap: function() {
        this.config.app.components.map.display();
      },
 
      /**
       * Initialise the menu date range controls to this app's default
       */
      initMenuDateRangeControls: function() {
        var item = {};
        item.time = this.getDefaultObservationQueryTimeParameters(item);
        this.config.app.components.menu.setDatepickerValues(item);
      },

      /**
       * Event handler for map feature-of-interest (FOI) select
       */
      sosMapFeatureOfInterestSelectHandler: function(evt) {
        var components = this.config.app.components;

        // Store map FOI selection in menu, & then display offerings for FOI
        components.menu.config.menu.selected = components.map.config.map.selected;
        components.menu.displayOfferings();
      },
 
      /**
       * Transform point in map component CRS to EPSG:4326
       */
      pointToLonLat: function(p1) {
        var map = this.config.app.components.map.config.map.object;
        var p2;

        if(p1) {
          p2 = new OpenLayers.Geometry.Point(p1.x, p1.y).transform(map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"));
        }

        return p2;
      },
 
      /**
       * Event handler for menu selection change
       */
      sosMenuChangeHandler: function(evt) {
        var components = this.config.app.components;
        var item;

        if(components.menu.config.menu.selected) {
          components.menu.updateCurrentItemDateRange();
          item = components.menu.getCurrentItem();
          item.time = this.getObservationQueryTimeParameters(item);
        }

        // Fetch & display observation data.  Update both plot & table
        if(this.haveRequiredObservationQueryParameters(item)) {
          jQuery.extend(true, this, {
            offeringId: item.offering.id,
            observedProperty: item.observedProperty,
            startDatetime: item.time.startDatetime,
            endDatetime: item.time.endDatetime
          });

          /* For those data models that have multiple station data per
             offering, we can specify the station via the selected FOI
             (if selected from map) */
          if(this.config.app.options.observation.useFoiId) {
            if(SOS.Utils.isValidObject(item.foi)) {
              jQuery.extend(true, this, {
                foiId: item.foi.id
              });
            } else {
              // Ensure we remove any reference to a previously selected FOI
              if(SOS.Utils.isValidObject(this.foiId)) {
                delete this.foiId;
              }
            }
          }

          // Optionally add to existing base plot/table.  Otherwise overwrite
          if(SOS.Utils.isValidObject(item.options)) {
            components.plot.config.mode.append = item.options.addToExisting;
            components.table.config.mode.append = item.options.addToExisting;
          }

          this.getObservationData();
        }
      },

      /**
       * Event handler for menu download data button click
       */
      sosMenuDownloadDataClickHandler: function(evt) {
        var components = this.config.app.components;
        var errorMessage = this.config.app.components.menu.config.menu.options.plotTableControlsSection.downloadData.errorMessage;

        // Only enable download if user has selected some data
        if(components.table.config.table.series.length > 0) {
          this.openDownloadDataDialog();
        } else {
          alert(errorMessage);
        }
      },

      /**
       * Open a dialog box to configure data format for download
       */
      openDownloadDataDialog: function() {
        var components = this.config.app.components;
        var dlOptions = this.config.app.components.menu.config.menu.options.plotTableControlsSection.downloadData;
        var m = new SOS.Menu();
        var panel = jQuery('<div/>');

        m.setMenuOptions({listBoxes: {size: 1}});

        // Construct the controls for configuring the data format
        var promptRow = jQuery('<div></div>', {
          "class": "sos-download-dialog-control-row"
        });
        var promptLabel = jQuery('<div></div>', {
          text: dlOptions.prompt
        });
        promptRow.append(promptLabel);
        panel.append(promptRow);

        var commentCharacterRow = jQuery('<div></div>', {
          "class": "sos-download-dialog-control-row"
        });
        var commentCharacterLabel = jQuery('<div></div>', {
          text: dlOptions.commentCharacterLabel
        });
        var commentCharacterList = jQuery('<div></div>', {
          "class": "sos-download-dialog-select-list",
          id: this.config.app.id + "DownloadDataDialogCommentCharacterList"
        });
        for(var i = 0, len = dlOptions.commentCharacters.length; i < len; i++) {
          var label = SOS.Utils.nonPrintingCharacterToLabel(dlOptions.commentCharacters[i]);
          var entry = {value: dlOptions.commentCharacters[i], label: label};
          m.config.menu.entries.push(entry);
        }
        m.initMenu(commentCharacterList);
        commentCharacterRow.append(commentCharacterLabel, commentCharacterList);
        panel.append(commentCharacterRow);
        m.config.menu.entries = [];

        var columnSeparatorRow = jQuery('<div></div>', {
          "class": "sos-download-dialog-control-row"
        });
        var columnSeparatorLabel = jQuery('<div></div>', {
          text: dlOptions.columnSeparatorLabel
        });
        var columnSeparatorList = jQuery('<div></div>', {
          "class": "sos-download-dialog-select-list",
          id: this.config.app.id + "DownloadDataDialogColumnSeparatorList"
        });
        for(var i = 0, len = dlOptions.columnSeparators.length; i < len; i++) {
          var label = SOS.Utils.nonPrintingCharacterToLabel(dlOptions.columnSeparators[i]);
          var entry = {value: dlOptions.columnSeparators[i], label: label};
          m.config.menu.entries.push(entry);
        }
        m.initMenu(columnSeparatorList);
        columnSeparatorRow.append(columnSeparatorLabel, columnSeparatorList);
        panel.append(columnSeparatorRow);
        m.config.menu.entries = [];

        var rowSeparatorRow = jQuery('<div></div>', {
          "class": "sos-download-dialog-control-row"
        });
        var rowSeparatorLabel = jQuery('<div></div>', {
          text: dlOptions.rowSeparatorLabel
        });
        var rowSeparatorList = jQuery('<div></div>', {
          "class": "sos-download-dialog-select-list",
          id: this.config.app.id + "DownloadDataDialogRowSeparatorList"
        });
        for(var i = 0, len = dlOptions.rowSeparators.length; i < len; i++) {
          var label = SOS.Utils.nonPrintingCharacterToLabel(dlOptions.rowSeparators[i]);
          var entry = {value: dlOptions.rowSeparators[i], label: label};
          m.config.menu.entries.push(entry);
        }
        m.initMenu(rowSeparatorList);
        rowSeparatorRow.append(rowSeparatorLabel, rowSeparatorList);
        panel.append(rowSeparatorRow);
        m.config.menu.entries = [];

        jQuery("body").after(panel);

        var self = this;
        var cc = jQuery('#' + this.config.app.id + 'DownloadDataDialogCommentCharacterList > .sos-menu-select-list');
        var cs = jQuery('#' + this.config.app.id + 'DownloadDataDialogColumnSeparatorList > .sos-menu-select-list');
        var rs = jQuery('#' + this.config.app.id + 'DownloadDataDialogRowSeparatorList > .sos-menu-select-list');

        var buttons = [
          {
            text: dlOptions.label,
            click: function() {
              var formatOptions = {};
              formatOptions.commentCharacter = cc.val();
              formatOptions.columnSeparator = cs.val();
              formatOptions.rowSeparator = rs.val();
              self.downloadTableSeriesData(formatOptions);
            }
          },
          {
            text: "Cancel",
            click: function() {jQuery(this).dialog().dialog("close");}
          }
        ];

        var dialog = panel.dialog({position: ['center', 'center'], buttons: buttons, title: dlOptions.label, width: 400, zIndex: 1010, stack: false});
        dialog.bind('dialogclose', function() {
          jQuery(this).dialog().dialog("destroy");
          jQuery(this).remove();
        });
      },

      /**
       * Download data of the current table series
       */
      downloadTableSeriesData: function(formatOptions) {
        var components = this.config.app.components;
        var t = {};

        // Format data suitable for download, then write it out
        if(components.table.config.table.series.length > 0) {
          jQuery.extend(true, formatOptions, components.table.config.table.options);
          components.table.generatePlainDataTable(t, components.table.config.table.series, formatOptions);

          if(SOS.Utils.isValidObject(t.tableText)) {
            document.open("text/html");
            document.write("<!DOCTYPE html>" + formatOptions.rowSeparator);
            document.write("<html><body><pre>" + formatOptions.rowSeparator);
            document.write(t.tableText);
            document.write("</pre></body></html>" + formatOptions.rowSeparator);
            document.close();
          }
        }
      },

      /**
       * Check that the given parameter is defined & not an empty string
       */
      isValidParameter: function(p) {
        return (SOS.Utils.isValidObject(p) && jQuery.trim(p) !== "");
      },

      /**
       * Check that an offering contains an observed property
       */
      offeringHasObservedProperty: function(offeringId, observedPropertyId) {
        var offering = this.sos.getOffering(offeringId);
        var retval = false;

        if(offering) {
          var ids = offering.getObservedPropertyIds();

          for(var i = 0, len = ids.length; i < len; i++) {
            if(ids[i] == observedPropertyId) {
              retval = true;
              break;
            }
          }
        }

        return retval;
      },

      /**
       * Check whether the given item object contains the required
       * parameters to perform a getObservation request
       */
      haveRequiredObservationQueryParameters: function(item) {
        return (SOS.Utils.isValidObject(item) &&
                SOS.Utils.isValidObject(item.offering) &&
                SOS.Utils.isValidObject(item.time) &&
                this.isValidParameter(item.offering.id) &&
                this.isValidParameter(item.observedProperty) &&
                this.isValidParameter(item.time.startDatetime) &&
                this.isValidParameter(item.time.endDatetime) &&
                this.offeringHasObservedProperty(item.offering.id, item.observedProperty));
      },

      /**
       * Extract the time parameters for performing a getObservation request
       * from the given item object, or use fallback defaults
       */
      getObservationQueryTimeParameters: function(item) {
        var time = {startDatetime: null, endDatetime: null};

        // If times have been explicitly set, we just use them
        if(SOS.Utils.isValidObject(item.time) && this.isValidParameter(item.time.startDatetime) && this.isValidParameter(item.time.endDatetime)) {
          time = item.time;
        } else {
          time = this.getDefaultObservationQueryTimeParameters(item);
        }

        return time;
      },

      /**
       * Get the fallback default time parameters for performing a
       * getObservation request
       */
      getDefaultObservationQueryTimeParameters: function(item) {
        var time = {startDatetime: null, endDatetime: null};

        /* Optionally we can take the full available times from the
           offering, however this could lead to performance problems */
        if(this.config.app.options.time.useOfferingTimePeriod) {
          if(SOS.Utils.isValidObject(item)) {
            if(SOS.Utils.isValidObject(item.offering)) {
              var offering = this.sos.getOffering(item.offering.id);
              time.startDatetime = offering.time.timePeriod.beginPosition;
              time.endDatetime = offering.time.timePeriod.endPosition;
            }
          }
        } else {
          // Fallback default: show data between configured ms ago up to now
          if(this.config.app.options.time.ms) {
            var t = {start: new Date(), end: new Date()};
            t = SOS.Utils.adjustTimeInterval(t, -this.config.app.options.time.ms, 0);
            time.startDatetime = t.start.toISOString();
            time.endDatetime = t.end.toISOString();
          }
        }

        return time;
      },
 
      /**
       * Get the observation data from the SOS according to this object's
       * properties, & then draw the plot, table etc.
       */
      getObservationData: function() {
        this._getOffering();

        if(this.haveValidOfferingObject()) {
          if(SOS.Utils.isValidObject(this.observedProperty)) {
            // If observed property is an array, fetch each in turn
            if(SOS.Utils.isArray(this.observedProperty)) {
              var components = this.config.app.components;
              components.plot.config.mode.append = true;
              components.table.config.mode.append = true;
              var p = this.observedProperty[components.plot.config.plot.series.length];

              if(p) {
                this.offering.filterObservedProperties(p);
              }
            } else {
              this.offering.filterObservedProperties(this.observedProperty);
            }
          }

          // The FOI will identify a given station in a multi-station offering
          if(this.config.app.options.observation.useFoiId) {
            if(SOS.Utils.isValidObject(this.foiId)) {
              this.offering.foiId = this.foiId;
            }
          }
          this.determineObservationQueryTimeParameters();
          this.offering.registerUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawObservationData});
          this.offering.getObservations(this.startDatetime, this.endDatetime);
        }
      },

      /**
       * Store and then display the retrieved observation data as a plot,
       * table etc.
       */
      drawObservationData: function() {
        // Avoid incremental calls to this function on subsequent event trigger
        this.offering.unregisterUserCallback({event: "sosObsAvailable", scope: this, callback: this.drawObservationData});
        var components = this.config.app.components;

        // Add these data to the data series
        this.storeObservationData();

        // If observed property is an array, fetch each in turn
        if(SOS.Utils.isArray(this.observedProperty)) {
          if(components.plot.config.plot.series.length < this.observedProperty.length) {
            this.getObservationData();
            return components.plot.config.plot.series.length;
          }
        }

        // Display the data series
        this.draw();
      },

      /**
       * Store the retrieved observation data in the plot, table etc.
       */
      storeObservationData: function() {
        var components = this.config.app.components;

        components.plot.offering = this.offering;
        components.table.offering = this.offering;

        // Add these data to the data series of the respective components
        components.plot.storeObservationData();
        components.table.storeObservationData();
      },

      /**
       * Display the given observation data (plot, table etc.)
       */
      draw: function() {
        var components = this.config.app.components;

        components.plot.offering = this.offering;
        components.table.offering = this.offering;

        // Make the plot tab the active tab, then draw the plot & table
        jQuery('#' + this.config.app.id + 'PlotPanel').html("");
        jQuery('#' + this.config.app.id + 'PlotTab a').trigger('click');
        components.plot.draw();

        jQuery('#' + this.config.app.id + 'TablePanel').html("");
        components.table.draw();

        // The plot & table share an overview, so that they talk to one another
        if(this.config.overview.options.show) {
          this.drawOverview();
          components.table.config.overview.object = components.plot.config.overview.object = this.config.overview.object;
          this.setupOverviewBehaviour();
        }

        // For external listeners (application-level plumbing)
        this.sos.events.triggerEvent("sosAppDrawObservationData");
      }
    });

    /**
     * SOS.App.Resources
     * Utility object for holding static resources used by the SOS.App class.
     * This keeps SOS.App tidier, as well as making it easier to override
     * these resources when specialising SOS.App
     */
    SOS.App.Resources = {
      config: {
        app: {
          options: {
            info: {
              metadata: {
                title: "Metadata",
                initialContent: "Welcome to SOS.App"
              },
              help: {
                title: "Help",
                initialContent: "Quick Start<ul><li>Select a Feature of Interest</li><li>Select an Offering from that Feature Of Interest</li><li>Select an Observed Property from that Offering</li></ul>The system will then plot/tabulate a rolling month's worth of data. Use the Controls to refine the query.",
                mapContent: "Map controls<ul><li>Click a point on the map to see available offerings</li><li>Zoom the map</li><ul><li>Shift+drag a region on the map to zoom in</li><li>Use the upper left '+' button to zoom in</li><li>Use the upper left '-' button to zoom out</li></ul><li>Drag to pan across the map</li><li>Click on the upper right '+' button to control available map layers</li><li>Click on the lower right '+' button to control the zoom region on an overview map</li></ul>",
                plotContent: "Plot controls<ul><li>Date Range</li><ul><li>Enter date as YYYY-MM-DD</li><li>Or pick from date selector</li></ul><li>Overplot further observed properties by checking Add To Existing</li><li>Subset the plot by dragging a selection across the plot (or across the lower overview plot); click anywhere on the overview plot to reset</li><li>Use the mouse scrollwheel to zoom in/out</li><li>Realign the plot vertically by dragging</li><li>Click on any two points on the plot to see summary statistics for data on that interval</li><li>Download the data via the Download Data button</li></ul>",
                tableContent: "Table controls<ul><li>Date Range</li><ul><li>Enter date as YYYY-MM-DD</li><li>Or pick from date selector</li></ul><li>Add further observed properties to the table by checking Add To Existing</li><li>Subset the table by dragging a selection down the table rows (or across the lower overview plot, or shift+click on any two rows on the table); click anywhere on the overview plot to reset</li><li>Click on any two rows on the table to see summary statistics for data on that interval</li><li>Download the data via the Download Data button</li></ul>"
              }
            }
          }
        }
      },
      CLASS_NAME: "SOS.App.Resources"
    }
  }
}

