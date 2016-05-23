/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
* Module:  SOS.MapSet.js
* Purpose: Extension of the User Interface library of the SOS project
* Author:  Paul M. Breen
* Date:    2013-06-28
* Id:      $Id$
******************************************************************************/

/* The SOS.Ui objects are built on top of SOS, OL & jquery.flot */
if(typeof OpenLayers !== "undefined" && OpenLayers !== null &&
   typeof SOS !== "undefined" && SOS !== null &&
   typeof SOS.Ui !== "undefined" && SOS.Ui !== null &&
   typeof jQuery !== "undefined" && jQuery !== null) {

  /* Create the SOS.MapSet namespace */
  if(typeof SOS.MapSet === "undefined") {
    /**
     * SOS.MapSet Class
     * Class for displaying a set of maps of data served from a SOS
     *
     * Inherits from:
     *  - <SOS.Ui>
     */
    SOS.MapSet = OpenLayers.Class(SOS.Ui, {
      maps: [],
      curr: null,
      config: null,
      CLASS_NAME: "SOS.MapSet",

      /**
       * Constructor for a SOS.MapSet object
       *
       * @constructor
       */
      initialize: function(sosOptions, mapSetOptions) {
        this.config = {
          mapSetLabels: ["World", "North", "South"],
          initMap: 0,
          useSwitcherPanel: true,
          useSwitcherAuto: false,
          switcherPanelNColumns: null,
          switcherPanel: {
            id: "sosMapSetSwitcherPanel",
            object: null
          },
          switchers: [],
          switcherAuto:{
            id: "sosMapSetSwitcherAuto",
            object: null,
            label: "Auto",
            checkedState: true
          }
        };
        var nmaps = this.config.mapSetLabels.length;

        // We can be given an alternative initial map to display
        if(SOS.Utils.isValidObject(mapSetOptions) && SOS.Utils.isValidObject(mapSetOptions.config) && SOS.Utils.isValidObject(mapSetOptions.config.initMap)) {
          this.config.initMap = mapSetOptions.config.initMap;
        }

        for(var i = 0, len = nmaps; i < len; i++) {
          this.maps[i] = new SOS.Map(sosOptions);
        }
        if(this.maps.length > 0) {
          this.setCurrentMap(this.config.initMap);
        }
        this.initMaps();

        for(var i = 0, len = nmaps; i < len; i++) {
          this.config.switchers[i] = {
            id: "sosMapSetSwitcher",
            object: null,
            label: this.config.mapSetLabels[i],
            checkedState: (i == this.config.initMap ? true : false)
          };
        }
        jQuery.extend(true, this, mapSetOptions);
      },

      /**
       * Destructor for a SOS.MapSet object
       * 
       * @destructor
       */
      destroy: function() {
      },

      /**
       * Set options for the map set
       */
      setMapSetOptions: function(options) {
        if(!SOS.Utils.isArray(options)) {
          options = [options];
        }
        for(var i = 0, len = this.maps.length; i < len; i++) {
          var opts = (i < options.length ? options[i] : options[0]);

          if(opts) {
            jQuery.extend(true, this.maps[i].config.map.options, opts);
          }
        }
      },

      /**
       * Set the current map to the map corresponding to the given index
       * from the maps array (modulo the length of the maps array)
       */
      setCurrentMap: function(i) {
        this.curr = this.maps[(i % this.maps.length)];
        this.curr.config.map.container = jQuery("#" + this.curr.config.map.id);
      },
 
      /**
       * Generate the mapset using this object's maps array
       */
      display: function(options) {
        // Parameters can optionally be tweaked for each call
        if(arguments.length > 0) {
          for(var i = 0, len = this.maps.length; i < len; i++) {
            jQuery.extend(true, this.maps[i].config.map.options, options);
          }
        }
        if(this.config.useSwitcherPanel) {
          this.initSwitcherPanel();
        }

        // Display currently selected map
        this.curr.display();
      },
  
      /**
       * Initialise the maps
       */
      initMaps: function() {
        // World map
        this.maps[0].setMapOptions({
          centre: new OpenLayers.LonLat(0, 0),
          defaultProjection: new OpenLayers.Projection("EPSG:4326"),
          params: {
            projection: "EPSG:4326",
            displayProjection: new OpenLayers.Projection("EPSG:4326"),
            eventListeners: {
              scope: this,
              "zoomend": this.mapZoomEndHandler
            }
          }
        });
        this.maps[0].setBaseLayerOptions({
          label: "OpenLayers WMS",
          url: "http://vmap0.tiles.osgeo.org/wms/vmap0?",
          params: {
            layers: ["basic"]
          }
        });
        this.maps[0].setOverviewOptions({show: true});

        // North map
        this.maps[1].setMapOptions({
          centre: new OpenLayers.LonLat(0, 90),
          params: {
            projection: "EPSG:32661",
            maxExtent: new OpenLayers.Bounds(-1350000.0,-1350000.0,5350000,5350000),
            eventListeners: {
              scope: this,
              "zoomend": this.mapZoomEndHandler
            }
          }
        });
        this.maps[1].setBaseLayerOptions({
          label: "NSIDC Atlas North",
          url: "http://nsidc.org/cgi-bin/atlas_north?",
          params: {
            bgcolor: "0x7391ad",
            layers: ["land", "sea_ice_extent_01", "snow_extent_01", "country_borders", "north_pole_geographic", "arctic_circle"]
          }
        });
        this.maps[1].setOverviewOptions({show: true});

        // South map
        this.maps[2].setMapOptions({
          centre: new OpenLayers.LonLat(0, -90),
          params: {
            projection: "EPSG:3031",
            maxExtent: new OpenLayers.Bounds(-5000000.0,-5000000.0,5000000.0,5000000.0),
            eventListeners: {
              scope: this,
              "zoomend": this.mapZoomEndHandler
            }
          }
        });
        this.maps[2].setBaseLayerOptions({
          label: "SCAR ADD",
          url: "http://add.antarctica.ac.uk/geoserver/add/wms?",
          params: {
            layers: ["add:timmermann_bathy_3031", "add:bamber_hillshade", "add:cst10_polygon", "add:cst10_linestring", "add:sgssi10_polygon", "add:macquarie10_polygon", "add:heard10_polygon", "add:outliers10_polygon", "add:graticule", "add:add_data_limit"]
          }
        });
        this.maps[2].setOverviewOptions({show: true});
      },
 
      /**
       * Initialise the switcher panel
       */
      initSwitcherPanel: function() {
        var panelContents = [];
        var sp = jQuery('#' + this.config.switcherPanel.id);

        // If switcher panel doesn't exist, create one on the fly
        if(sp.length < 1) {
          sp = jQuery('<form id="' + this.config.switcherPanel.id + '"/>');
          jQuery('body').append(sp);
          this.curr.config.map.container.after(sp);
        }
        this.config.switcherPanel.object = sp;

        // Construct the map set switcher controls
        for(var i = 0, len = this.config.switchers.length; i < len; i++) {
          this.config.switchers[i].object = jQuery('<input/>', {
            type: "radio",
            name: this.config.switchers[i].id,
            id: this.config.switchers[i].id,
            value: this.config.switchers[i].label
          });
          this.config.switchers[i].object.prop("checked", this.config.switchers[i].checkedState);
        }

        if(this.config.useSwitcherAuto) {
          this.config.switcherAuto.object = jQuery('<input/>', {
            type: "checkbox",
            name: this.config.switcherAuto.id,
            id: this.config.switcherAuto.id,
            value: this.config.switcherAuto.label
          });
          this.config.switcherAuto.object.prop("checked", this.config.switcherAuto.checkedState);
        }

        for(var i = 0, len = this.config.switchers.length; i < len; i++) {
          panelContents.push(this.config.switchers[i].object);
          panelContents.push('<span>' + this.config.switchers[i].label + '</span>');

          // Optionally arrange controls in an N-column grid
          if(this.config.switcherPanelNColumns) {
            if(((i + 1) % this.config.switcherPanelNColumns) == 0) {
              panelContents.push('<br/>');
            }
          }
        }

        if(this.config.useSwitcherAuto) {
          panelContents.push(this.config.switcherAuto.object);
          panelContents.push('<span>' + this.config.switcherAuto.label + '</span>');
        }

        this.config.switcherPanel.object.append.apply(this.config.switcherPanel.object, panelContents);

        jQuery('form input:radio[name=' + this.config.switchers[0].id + ']').change({self: this}, this.mapSwitcherChangeHandler);
      },
 
      /**
       * Event handler for map switcher change
       */
      mapSwitcherChangeHandler: function(evt) {
        var self = evt.data.self;
        var value = jQuery(this).val();

        // Switch maps according to control selected by user
        for(var i = 0, len = self.config.switchers.length; i < len; i++) {
          if(value == self.config.switchers[i].label) {
            self.setCurrentMap(i);
            break;
          }
        }
        self.curr.config.map.container.empty();
        self.curr.display();
      },

      /**
       * Event handler for map zoomEnd
       */
      mapZoomEndHandler: function(evt) {
        /* There can be a zoom event on initial load (e.g., by a call to
           zoomToMaxExtent()), but as this isn't a zoom performed by the user,
           we don't want to automatically switch maps */
        if(this.curr.config.isInitLoad) {
          this.curr.config.isInitLoad = false;
        } else {
          /* Get the centre lon/lat (accounting for CRS) of current zoomed
             region, find the map in the mapset that's nearset to this point,
             then switch to that map (if it's different to the current map) */
          if(this.switcherIsInAutoMode()) {
            var c1 = this.getCentreLonLatOfCurrentExtent();
            var n = this.getMapIndexNearestToLonLat(c1);
            this.switchToMapIndex(n);
          }
        }
      },

      /**
       * Check whether the map switcher is in auto mode
       */
      switcherIsInAutoMode: function() {
        var autoMode = false;

        // In auto mode, we switch maps according to proximity to zoomed region
        if(this.config.useSwitcherAuto) {
          var sa = jQuery('#' + this.config.switcherAuto.id);
          autoMode = (sa && sa.is(':checked'));
        }

        return autoMode;
      },

      /**
       * Get the centre lon/lat (accounting for CRS) of current map extent
       */
      getCentreLonLatOfCurrentExtent: function() {
        var c;

        if(this.curr.config.map.object) {
          c = this.curr.config.map.object.getExtent().clone().transform(this.curr.config.map.object.getProjectionObject(), new OpenLayers.Projection("EPSG:4326")).getCenterLonLat();
        }

        return c;
      },

      /**
       * Get the maps array index of the map whose centre is the closest to
       * the given lon/lat
       */
      getMapIndexNearestToLonLat: function(c1) {
        var c2, n, dists = [];

        /* Get centre lon/lat of each map in mapset, & calculate the
           nearest centre to the given lon/lat */
        if(c1) {
          for(var i = 0, len = this.maps.length; i < len; i++) {
            c2 = this.maps[i].config.map.options.centre;

            if(c2) {
              var d = OpenLayers.Util.distVincenty(c1, c2);
              dists.push(d);

              if(Math.min.apply(null, dists) == d) {
                n = i;
              }
            }
          }
        }

        return n;
      },

      /**
       * Switch to the map corresponding to the given maps array index (only
       * if it's different to the current map)
       */
      switchToMapIndex: function(n) {
        if(SOS.Utils.isValidObject(n) && n >= 0 && n < this.maps.length) {
          if(this.curr !== this.maps[n]) {
            this.curr.config.map.container.empty();
            this.setCurrentMap(n);

            /* If the original map handles the zoomend event first, then we
               end up with two maps instead of the switched map replacing
               the original.  This late check for emptiness is to overcome
               that situation */
            if(!jQuery.trim(this.curr.config.map.container.html()).length) {
              this.curr.display();
            } else {
              this.curr.config.map.container.empty();
              this.curr.display();
            }
            this.config.switchers[n].object.prop("checked", true);
          }
        }
      }
    });
  }
}

