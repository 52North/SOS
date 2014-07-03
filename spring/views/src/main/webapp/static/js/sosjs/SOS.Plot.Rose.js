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

/******************************************************************************
* Project: SOS
* Module:  SOS.Plot.Rose.js
* Purpose: Extension of the User Interface library of the SOS project
* Author:  Paul M. Breen
* Date:    2013-07-30
* Id:      $Id$
******************************************************************************/

/* The SOS.Ui objects are built on top of SOS, OL & jquery.flot */
if(typeof OpenLayers !== "undefined" && OpenLayers !== null &&
   typeof SOS !== "undefined" && SOS !== null &&
   typeof SOS.Ui !== "undefined" && SOS.Ui !== null &&
   typeof jQuery !== "undefined" && jQuery !== null &&
   typeof jQuery.plot !== "undefined" && jQuery.plot !== null) {

  /* Create the SOS.Plot.Rose namespace */
  if(typeof SOS.Plot.Rose === "undefined") {
    /**
     * SOS.Plot.Rose Class
     * Class for displaying a rose plot of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Plot>
     */
    SOS.Plot.Rose = OpenLayers.Class(SOS.Plot, {
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
      CLASS_NAME: "SOS.Plot.Rose",

      /**
       * Constructor for a SOS.Plot.Rose object
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
            id: "sosPlotRose",
            series: [],
            options: {
              show: true,
              series:{
                lines: {show: false, fill: false},
                points: {show: false},
                bars: {show: false},
                rose: {
                  active: true,
                  show: true,
                  drawGrid: {
                    gridMode: "ticks",
                    labelPos: 0,
                    drawValue: true
                  }
                }
              },
              grid: {
                tickLabel: ["E","","S","","W","","N",""]
              }
            },
            transformDirection: this.transformDirection,
            annotations: {
              show: true,
              titleLabel: "Frequency of Counts by Direction / %",
              meanLabel: "mean = ",
              calmLabel: "calm = "
            }
          },
          overview: {
            object: null,
            id: "sosPlotRoseOverview",
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
            },
            rose: {
              Nd: 12,
              f_max: 5,
              sectorMargin: 5
            }
          },
          messages: {
            noDataForDateRange: "No data available for given dates."
          },
          mode: {append: true}
        };
        jQuery.extend(true, this, options);
      },

      /**
       * Plot the given observation data
       */
      draw: function() {
        var mag, dir;

        // Find the magnitude & direction from the retrieved data
        for(var i = 0, len = this.config.plot.series.length; i < len; i++) {
          if(/Speed/i.test(this.config.plot.series[i].name)) {
            mag = this.config.plot.series[i];
          } else if(/Direction/i.test(this.config.plot.series[i].name)) {
            dir = this.config.plot.series[i];
          }
        }

        if(this.config.plot.options.show) {
          // Construct the data required by the rose plot
          if(mag && dir) {
            var series = this.constructRoseData(mag, dir, this.config.format.rose);
            var stats = this.computeDataStats(series, 0, this.config.format.rose);
            this.setPlotOptions({
              series: {
                rose: {
                  dataMin: stats.dataMin,
                  dataMax: stats.dataMax
                }
              }
            });

            // Generate the plot
            this.config.plot.object = jQuery.plot(jQuery('#' + this.config.plot.id), series, this.config.plot.options);

            // Optionally annotate the plot
            if(this.config.plot.annotations.show) {
              this.drawAnnotations(mag);
            }

            // Optionally generate the plot overview
            if(this.config.overview.options.show) {
              this.drawOverview();
            }
          } else {
            var container = jQuery('#' + this.config.plot.id);
            container.html(this.formatInformationMessage(this.config.messages.noDataForDateRange));
          }
        }
      },

      /**
       * Add annotations to the plot
       */
      drawAnnotations: function(mag) {
        var stats = this.computeDataStats([mag], 1);
        this.drawTitle();
        this.drawAnnotationBox();
        this.drawMeanLabel(stats.mean, mag.uomTitle);
        this.drawCalmLabel(stats.calm, "%");
      },

      /**
       * Add the title to the plot
       */
      drawTitle: function() {
        var ac = jQuery('#' + this.config.plot.id);

        if(ac.length > 0) {
          ac.append(jQuery("<div></div>", {
            id: this.config.plot.id + "AnnotationTitle",
            "class": "sos-plot-rose-annotation-title",
            html: this.config.plot.annotations.titleLabel
          }));
        }
      },

      /**
       * Add a floated box to align the plot annotations
       */
      drawAnnotationBox: function() {
        var ac = jQuery('#' + this.config.plot.id);

        if(ac.length > 0) {
          ac.append(jQuery("<div></div>", {
            id: this.config.plot.id + "AnnotationBox",
            "class": "sos-plot-rose-annotation-box"
          }));
        }
      },

      /**
       * Add an annotation to the plot detailing the mean wind speed
       */
      drawMeanLabel: function(mean, uom) {
        var ac = jQuery('#' + this.config.plot.id + "AnnotationBox");
        var fv = this.config.format.value;

        if(ac.length > 0) {
          ac.append(jQuery("<div></div>", {
            id: this.config.plot.id + "AnnotationMean",
            "class": "sos-plot-rose-annotation-mean",
            html: this.config.plot.annotations.meanLabel + fv.formatter(parseFloat(mean), fv.sciLimit, fv.digits) + " " + uom
          }));
        }
      },

      /**
       * Add an annotation to the plot detailing calm winds (as a percentage)
       */
      drawCalmLabel: function(calm, uom) {
        var ac = jQuery('#' + this.config.plot.id + "AnnotationBox");
        var fv = this.config.format.value;

        if(ac.length > 0) {
          ac.append(jQuery("<div></div>", {
            id: this.config.plot.id + "AnnotationCalm",
            "class": "sos-plot-rose-annotation-calm",
            html: this.config.plot.annotations.calmLabel + fv.formatter(parseFloat(calm), fv.sciLimit, fv.digits) + " " + uom
          }));
        }
      },

      /**
       * Transform the given direction for the flot rose plot coordinate system
       */
      transformDirection: function(theta) {
        /* N.B.: Javascript's '%' operator is not modulus; it can return
                 negative numbers. */
        return((((theta - 90) % 360) + 360) % 360);
      },

      /**
       * Construct the data series required by flot rose plots
       */
      constructRoseData: function(mag, dir, options) {
        var options = options || {Nd: 12, f_max: 5, sectorMargin: 5};
        var w = (360 / options.Nd) / 2;
        var series = [];
        var magMax = 0;

        /* Calculate the maximum magnitude of the given data, rounded up to
           a multiple of f_max */
        if(mag.data.length) {
          magMax = Math.max.apply(null, SOS.Utils.extractColumn(mag.data, 1));
          magMax = options.f_max * Math.ceil(magMax / options.f_max);
        }

        /* Merge magnitude & direction so each vector is (t, A, theta),
           then sort in direction increasing */
        for(var i = 0, len = mag.data.length; i < len; i++) {
          if(dir.data[i]) {
            mag.data[i][2] = dir.data[i][1];
          }
        }

        mag.data.sort(function(a, b) {
          var ret = 0;

          if(parseFloat(a[2]) < parseFloat(b[2])) {
            ret = -1;
          } else if(parseFloat(a[2]) > parseFloat(b[2])) {
            ret = 1;
          }

          return ret;
        });

        var sectors = [], j = 0;

        // Collect vectors into Nd sectors of arc length 2w
        for(var i = 0; i < options.Nd; i++) {
          var centre = i * 2 * w;
          var lower = centre - w;
          var upper = centre + w;
          var sector = [];

          for(len = mag.data.length; j < len; j++) {
            if(parseFloat(mag.data[j][2]) < upper) {
              sector.push(mag.data[j]);
            } else {
              break;
            }
          }
          sectors.push(sector);
        }

        for(len = mag.data.length; j < len; j++) {
          if(parseFloat(mag.data[j][2]) <= 360) {
            sectors[0].push(mag.data[j]);
          } else {
            break;
          }
        }

        /* N.B.: Our data have North as 0 degrees, whereas flot rose plots have
           (the more usual) East as 0 degrees.  However, just to complicate
           things further, flot rose plots move clockwise around the circle! */

        /* Construct the data series, consisting of even-width sectors, with
           the data within each sector grouped by magnitude.  The actual value
           for each grouping, is the frequency count of those data that have
           the given magnitude within the given sector, expressed as a
           percentage */
        for(var j = 0, c = 0; j < magMax; j += options.f_max) {
          var label = j + " to " + (j + options.f_max) + " / " + mag.uomTitle;
          var data = [];

          for(var i = 0; i < options.Nd; i++) {
            var centre = this.config.plot.transformDirection(i * 2 * w);
            var lower = centre - w + options.sectorMargin;
            var upper = centre + w - options.sectorMargin;
            var value = 0;

            if(mag.data.length) {
              for(var k = 0, len = sectors[i].length; k < len; k++) {
                if(sectors[i][k][1] >= j && sectors[i][k][1] < j + options.f_max) {
                  value++;
                }
              }
              value = (value / mag.data.length) * 100;
            }
            data.push([value, lower, upper]);
          }
          series.push({label: label, color: c++, rose: {show: true}, data: data});
        }

        return series;
      },

      /**
       * Compute summary statistics of the given data series column
       */
      computeDataStats: function(series, column, options) {
        var options = options || {f_max: 5};
        var values = [];

        for(var i = 0, slen = series.length; i < slen; i++) {
          values = values.concat(SOS.Utils.extractColumn(series[i].data, column));
        }
        var stats = SOS.Utils.computeStats(values);
        stats.dataMin = 0;
        stats.calm = this.computeCalmStats(values).calm;

        // Round dataMax to nearest multiple of f_max
        if(stats.max > 0) {
          stats.dataMax = options.f_max * Math.ceil(stats.max / options.f_max);
        }

        return stats;
      },

      /**
       * Compute statistics for calm winds, expressed as a percentage.
       * Calm conditions are defined as winds < 1 mile/hour (0.447 m/s)
       */
      computeCalmStats: function(values, options) {
        var options = options || {threshold: 0.447};
        var stats = {threshold: options.threshold, count: 0, calm: 0};

        for(var i = 0, len = values.length; i < len; i++) {
          if(values[i] < options.threshold) {
            stats.count++;
          }
        }

        if(values.length > 0) {
          stats.calm = (stats.count / values.length) * 100;
        }

        return stats;
      }
    });
  }
}

