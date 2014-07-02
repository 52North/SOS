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
* Module:  SOS.Plot.Stuve.js
* Purpose: Extension of the User Interface library of the SOS project
* Author:  Paul M. Breen
* Date:    2014-01-28
* Id:      $Id$
******************************************************************************/

/* The SOS.Ui objects are built on top of SOS, OL & jquery.flot */
if(typeof OpenLayers !== "undefined" && OpenLayers !== null &&
   typeof SOS !== "undefined" && SOS !== null &&
   typeof SOS.Ui !== "undefined" && SOS.Ui !== null &&
   typeof jQuery !== "undefined" && jQuery !== null &&
   typeof jQuery.plot !== "undefined" && jQuery.plot !== null) {

  /* Create the SOS.Plot.Stuve namespace */
  if(typeof SOS.Plot.Stuve === "undefined") {
    /**
     * SOS.Plot.Stuve Class
     * Class for displaying a Stuve plot of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Plot>
     */
    SOS.Plot.Stuve = OpenLayers.Class(SOS.Plot, {
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
      CLASS_NAME: "SOS.Plot.Stuve",

      /**
       * Constructor for a SOS.Plot.Stuve object
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
            id: "sosPlotStuve",
            series: [],
            options: {
              show: true,
              xaxis: {mode: null, axisLabel: "Temperature / &deg;C", min: -90, max: 40},
              yaxis: {min: 100, max: 1050, tickSize: 50, ticks: this.generateYaxisTicks, axisLabel: "Pressure / hPa"},
              series: {
                lines: {show: true, fill: false},
                points: {show: false},
                bars: {show: false}
              },
              grid: {borderWidth: 1, hoverable: true, clickable: true},
              hooks: {drawSeries: [this.markDryAdiabats]}
            }
          },
          overview: {
            object: null,
            id: "sosPlotStuveOverview",
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
          metadata: {
            launch: {
              datetime: null
            },
            columns: {
              names: []
            }
          },
          constants: {
            P0: 1000,                       // Reference pressure (hPa)
            K0: -273.15,                    // Temperature deg C to K
            k: 0.28571,                     // k = R/Cp
            m2kn: 1.94384449                // m/s to knots
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
            /* Wind barb dimensions from:
               http://www.ncarg.ucar.edu//supplements/wmap/index.html#MARKER-9-470
            */
            windBarb: {
              speeds: [50, 10, 5],
              shaft: {len: 30},
              flag: {height: 10, base: 6, space: 6},
              barb: {space: 3, angle: 28},
              speedToBarbLength: function(s) {
                return(s == 10 ? this.shaft.len / 3 : this.shaft.len / 6);
              }
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
       * Calculate potential temperature at given temperature and pressure
       * reference points
       */
      theta: function(T, P) {
        var theta = (T - this.config.constants.K0) * Math.pow((this.config.constants.P0 / P), this.config.constants.k) + this.config.constants.K0;
        return theta;
      },

      /**
       * Calculate pressure at given temperature and potential temperature
       * reference points
       */
      P: function(T, theta) {
        var P = this.config.constants.P0 / Math.pow((theta - this.config.constants.K0) / (T - this.config.constants.K0), (1 / this.config.constants.k));
        return P;
      },

      /**
       * Set the y-axis to reverse P^k
       */
      setYAxisReversePk: function() {
        var axis = "yaxis";
        var k = this.config.constants.k;
        this.config.plot.options[axis].transform = function(v) {return -Math.pow(v, k);};
        this.config.plot.options[axis].inverseTransform = function(v) {return -Math.pow(v, 1/k);};
        this.config.overview.options[axis].transform = this.config.plot.options[axis].transform;
        this.config.overview.options[axis].inverseTransform = this.config.plot.options[axis].inverseTransform;
      },

      /**
       * Display summary stats about the given selected observation data
       *
       * N.B.: For these plots, our dependent data are on the x-axis, hence
       *       the need to override this function
       */
      displaySelectedIntervalStats: function(container, selected) {
        var series = selected[0].item.series;
        var start = Math.min(selected[0].item.dataIndex, selected[1].item.dataIndex);
        var end = Math.max(selected[0].item.dataIndex, selected[1].item.dataIndex);
        var subset = series.data.slice(start, end + 1);
        var values = SOS.Utils.extractColumn(subset, 0);   // x-axis, not y
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
       * Plot the given observation data
       */
      draw: function() {
        var T, P, Td, W = {mag: null, dir: null};

        // Find the components from the retrieved data
        for(var i = 0, len = this.config.plot.series.length; i < len; i++) {
          if(/Dew Point Temperature/i.test(this.config.plot.series[i].name)) {
            Td = this.config.plot.series[i];
          } else if(/Air Temperature/i.test(this.config.plot.series[i].name)) {
            T = this.config.plot.series[i];
          } else if(/Air Pressure/i.test(this.config.plot.series[i].name)) {
            P = this.config.plot.series[i];
          } else if(/Wind Speed/i.test(this.config.plot.series[i].name)) {
            W.mag = this.config.plot.series[i];
          } else if(/Wind Direction/i.test(this.config.plot.series[i].name)) {
            W.dir = this.config.plot.series[i];
          }
        }

        if(this.config.plot.options.show) {
          // Construct the data table required by the Stuve plot & store
          if(T && P && Td) {
            var series = this.constructStuveMainData(P, T, Td);
            this.config.plot.series = series;
            this.clearSeriesMetadata();
            this.storeSeriesMetadataLaunch(P);
            this.storeSeriesMetadataColumns(P, T, Td);

            this.setYAxisReversePk();

            // Generate the plot
            this.config.plot.object = jQuery.plot(jQuery('#' + this.config.plot.id), series, this.config.plot.options);

            // Add wind data to the plot
            if(W.mag && W.dir) {
              var windSeries = this.constructStuveWindData(P, W);
              this.config.plot.windSeries = windSeries;
              this.storeSeriesMetadataColumns(W.mag, W.dir);

              if(windSeries.length > 0) {
                var ctx = this.config.plot.object.getCanvas().getContext("2d");
                this.drawWind(this.config.plot.object, ctx, windSeries[0]);
              }
            }

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

            // For external listeners (application-level plumbing)
            this.sos.events.triggerEvent("sosPlotStuveDrawObservationData");
          } else {
            var container = jQuery('#' + this.config.plot.id);
            container.html(this.formatInformationMessage(this.config.messages.noDataForDateRange));
          }
        }
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
            var metadata = evt.data.self.config.metadata;
            // The small offsets avoid flickering when box is under mouse
            var x = pos.pageX + 20;
            var y = pos.pageY + 20;
            var T = item.datapoint[0];
            var P = item.datapoint[1];
            var html = jQuery('<p>'
            + '<span class="sos-control-title">Balloon Launch Time:</span> <span>' + ft.formatter(metadata.launch.datetime) + '</span><br/>'
            + '<span class="sos-control-title">Temperature:</span> <span>' + fv.formatter(T, fv.sciLimit, fv.digits) + ' ' + '&deg;C' + '</span><br/>'
            + '<span class="sos-control-title">Pressure:</span> <span>' + fv.formatter(P, fv.sciLimit, fv.digits) + ' ' + 'hPa' + '</span><br/>'
            + '<span class="sos-control-title">Potential Temperature:</span> <span>' + fv.formatter(evt.data.self.theta(T, P), fv.sciLimit, fv.digits) + ' ' + '&deg;C' + '</span><br/>'
            + '</p>');
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

              // Reinstate wind data
              if(self.config.plot.windSeries.length > 0) {
                var ctx = self.config.plot.object.getCanvas().getContext("2d");
                self.drawWind(self.config.plot.object, ctx, self.config.plot.windSeries[0]);
              }
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

        // Unbind particular events from plot, & all events from overview
        if(p.length > 0) {
          p.unbind("plotselected");
        }
        if(o.length > 0) {
          o.unbind();
        }
      },

      /**
       * Generate required tickmarks for the yaxis
       */
      generateYaxisTicks: function() {
        var ticks = [];

        // N.B.: We can't see plot options from here, so we configure axis here
        var yaxis = {min: 100, max: 1050, tickSize: 50};

        for(var i = yaxis.min, len = yaxis.max; i < len; i += yaxis.tickSize) {
          var t = (i % (2 * yaxis.tickSize) == 0 ? [i, i] : [i, ""]);
          ticks.push(t);
        }

        return ticks;
      },

      /**
       * Add the dry adiabats gridlines to the plot
       */
      markDryAdiabats: function(plot, ctx) {
        var edge = plot.getPlotOffset();
        var clip = {x: edge.left, y: edge.top, w: ctx.canvas.width-edge.right, h: ctx.canvas.height};
        var off;

        /* N.B.: Using rect() to clip doesn't always work, e.g., when the plot
                 is over from the left edge of the page */

        // Clip plotting region so that dry adiabats don't extend beyond border
        ctx.save();
        ctx.beginPath();
        ctx.moveTo(clip.x, clip.y);
        ctx.lineTo(clip.w, clip.y);
        ctx.lineTo(clip.w, clip.h);
        ctx.lineTo(clip.x, clip.h);
        ctx.closePath();
        ctx.clip();

        ctx.strokeStyle = "#dddddd";

        /* The dry adiabat runs from the (actual) temperature at 1000 hPa to
           the point at T = 0, P^k = 0 */
        for(var x = -270; x <= 100; x += 10) {
          ctx.beginPath();
          off = plot.pointOffset({x: -273.15, y: 0});
          ctx.moveTo(off.left, off.top);

          off = plot.pointOffset({x: x, y: 1000});
          ctx.lineTo(off.left, off.top);
          ctx.stroke();
        }

        // Restore (from clipping region)
        ctx.restore();
      },

      /**
       * Plot the given wind data as wind barbs
       */
      drawWind: function(plot, ctx, table) {
        var axes = plot.getAxes();

        for(var i = 0, len = table.data.length; i < len; i++) {
          ctx.save();

          var x = axes.xaxis.max - 20;
          var y = table.data[i][2];
          var m = table.data[i][0];
          var d = table.data[i][1];
          var off = plot.pointOffset({x: x, y: y});

          // Wind directions are given as degrees from North
          ctx.translate(off.left, off.top);
          ctx.rotate(-(Math.PI / 2) + (d / 180) * Math.PI);

          // Convert wind speed in m/s to knots for drawing the wind barbs
          var knots = m * this.config.constants.m2kn;
          this._drawWindBarb(ctx, knots, this.config.format.windBarb);

          ctx.restore();
        }
      },

      /**
       * Plot the given wind speed as a wind barb
       */
      _drawWindBarb: function(ctx, mag, windBarb) {
        var end = windBarb.shaft.len;

        if(mag >= windBarb.speeds[windBarb.speeds.length-1]) {
          this._drawWindBarbShaft(ctx, end);
        }

        for(var i = 0, slen = windBarb.speeds.length; i < slen; i++) {
          var limit = windBarb.speeds[i];
          var n = Math.floor(mag / limit);

          for(var j = 0; j < n; j++) {
            if(mag >= windBarb.speeds[0]) {
              this._addFlagToWindBarb(ctx, end, windBarb.flag.height, windBarb.flag.base);
              end -= windBarb.flag.space;
            } else {
              var blen = windBarb.speedToBarbLength(limit);
              this._addBarbToWindBarb(ctx, end, blen, windBarb.barb.angle);
              end -= windBarb.barb.space;
            }
            mag -= limit;
          }
        }
      },

      /**
       * Draw a wind barb shaft for the given wind speed
       */
      _drawWindBarbShaft: function(ctx, len) {
        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(len, 0);
        ctx.stroke();
      },

      /**
       * Add a flag to the current wind barb
       */
      _addFlagToWindBarb: function(ctx, end, height, base) {
        ctx.beginPath();
        ctx.moveTo(end, 0);
        ctx.lineTo(end - (base / 2), + height);
        ctx.lineTo(end - base, 0);
        ctx.fill();
      },

      /**
       * Add a barb to the current wind barb
       */
      _addBarbToWindBarb: function(ctx, end, len, angle) {
        ctx.save();

        ctx.translate(end, 0);
        ctx.rotate((Math.PI / 2) - (angle / 180) * Math.PI);

        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(len, 0);
        ctx.stroke();

        ctx.restore();
      },
 
      /**
       * Construct the main data series required by Stuve plots
       */
      constructStuveMainData: function(P, T, Td) {
        var series = [];

        series.push(this._constructStuveMainData(T, P));
        series.push(this._constructStuveMainData(Td, P));

        return series;
      },

      /**
       * Construct the main data series required by Stuve plots
       */
      _constructStuveMainData: function(x, y) {
        var data = [], table = {};

        // Merge cotemporal values
        for(var i = 0, xlen = x.data.length; i < xlen; i++) {
          for(var j = 0, ylen = y.data.length; j < ylen; j++) {
            if(y.data[j] && (y.data[j][0] == x.data[i][0])) {
              data.push([x.data[i][1], y.data[j][1], x.data[i][0]]);
            }
          }
        }

        /* Copy over all the metadata properties from the x variable,
           then add the data */
        for(var p in x) {
          if(p != "data") {
            table[p] = x[p];
          }
        }
        table.data = data;

        return table;
      },

      /**
       * Construct the wind data series required by Stuve plots
       */
      constructStuveWindData: function(P, W) {
        var data = [], table = {}, series = [];

        // Merge cotemporal values
        for(var i = 0, Wmlen = W.mag.data.length; i < Wmlen; i++) {
          for(var j = 0, Plen = P.data.length; j < Plen; j++) {
            if(P.data[j] && (P.data[j][0] == W.mag.data[i][0])) {
              data.push([W.mag.data[i][1], W.dir.data[i][1], P.data[j][1]]);
            }
          }
        }

        /* Copy over all the metadata properties from the Wind Speed series,
           then add the data */
        for(var p in W.mag) {
          if(p != "data") {
            table[p] = W.mag[p];
          }
        }
        table.data = data;
        series.push(table);

        return series;
      },
 
      /**
       * Clear the series metadata
       */
      clearSeriesMetadata: function() {
        this.config.metadata.launch.datetime = null;
        this.config.metadata.columns.names = [];
      },
  
      /**
       * Store launch metadata from the given data series
       */
      storeSeriesMetadataLaunch: function(series) {
        var launch = this.config.metadata.launch;
        var ft = this.config.format.time;

        // Launch datetime is only recorded to the hour
        var d = new Date(series.data[0][0]);
        d.setUTCMilliseconds(0);
        d.setUTCSeconds(0);
        d.setUTCMinutes(0);

        launch.datetime = ft.formatter(d.getTime());
      },
  
      /**
       * Store columns metadata from the given data series
       */
      storeSeriesMetadataColumns: function() {
        var names = this.config.metadata.columns.names;

        for(var i = 0, len = arguments.length; i < len; i++) {
          names.push(arguments[i].name + " / " + arguments[i].uomTitle);
        }
      },
  
      /**
       * Construct a series of all data, with Pressure as the independent
       * variable
       */
      constructPressureSeries: function() {
        var table = this.initDataTable();
        var series = [], data = [], set1 = [], set2 = [], pcol = [];

        /* We take the pressure/temperature data series, & then the
           pressure/wind data series, & combine the superset of these */
        if(this.config.plot.series.length > 0) {
          var P = SOS.Utils.extractColumn(this.config.plot.series[0].data, 1);
          var T = SOS.Utils.extractColumn(this.config.plot.series[0].data, 0);

          if(this.config.plot.series.length > 1) {
            var Td = SOS.Utils.extractColumn(this.config.plot.series[1].data, 0);
            for(var i = 0, len = P.length; i < len; i++) {
              set1.push([P[i], T[i], Td[i]]);
            }

            if(this.config.plot.windSeries.length > 0) {
              var W = {mag: null, dir: null, P: null};
              W.mag = SOS.Utils.extractColumn(this.config.plot.windSeries[0].data, 0);
              W.dir = SOS.Utils.extractColumn(this.config.plot.windSeries[0].data, 1);
              W.P = SOS.Utils.extractColumn(this.config.plot.windSeries[0].data, 2);

              for(var i = 0, len = W.P.length; i < len; i++) {
                set2.push([W.P[i], W.mag[i], W.dir[i]]);
              }

              // Get a unique, reverse-sorted superset of pressure values
              var pcol = SOS.Utils.extractColumn(set1, 0);
              pcol = pcol.concat(SOS.Utils.extractColumn(set2, 0));
              pcol.sort(function(a, b) {return b - a;});
              pcol = SOS.Utils.getUniqueList(pcol);

              var max = Math.max(set1.length, set2.length);

              // Combine into a sparse table (left outer join)
              for(var i = 0, len = pcol.length; i < len; i++) {
                data[i] = ["", "", "", "", ""];
                data[i][0] = pcol[i];

                for(var j = 0; j < max; j++) {
                  if(j < set1.length && pcol[i] == set1[j][0]) {
                    data[i][1] = set1[j][1];
                    data[i][2] = set1[j][2];
                  }
                  if(j < set2.length && pcol[i] == set2[j][0]) {
                    data[i][3] = set2[j][1];
                    data[i][4] = set2[j][2];
                  }
                }
              }
            }
          }

          // Setup metadata properties for this table
          if(this.haveValidOfferingObject()) {
            table.label = this.offering.name;
            if(table.label.length > 0) table.label += " - ";
            table.label += this.config.metadata.launch.datetime;
            table.headerLabel = table.label;
          }
          table.columns = this.config.metadata.columns;
          table.data = data;
          series.push(table);
        }

        return series;
      }
    });
  }
}

