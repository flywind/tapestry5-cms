/**
 * 图片轮播插件
 * 包括轮播插件核心slider.js和延时加载组件Layzr.js
 * */
;(function($){
	
	
	/* #Photo slider core
	 * 图片轮播插件
	================================================== */
	/*---slider start---*/
	//;(function($){
		$.fn.exists = function() {
			if ($(this).length > 0) {
				return true;
			} else {
				return false;
			}
		}
	
		$.fn.loaded = function(callback, jointCallback, ensureCallback){
			var len = this.length;
			if (len > 0) {
				return this.each(function() {
					var el    = this,
						$el  = $(el),
						blank = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
	
					$el.on("load.dt", function(event) {
						$(this).off("load.dt");
						if (typeof callback == "function") {
							callback.call(this);
						}
						if (--len <= 0 && (typeof jointCallback == "function")){
							jointCallback.call(this);
						}
					});
	
					if (!el.complete || el.complete === undefined) {
						el.src = el.src;
					} else {
						$el.trigger("load.dt")
					}
				});
			} else if (ensureCallback) {
				if (typeof jointCallback == "function") {
					jointCallback.call(this);
				}
				return this;
			}
		};
	
		$.rsCSS3Easing = {
			easeOutSine: 'cubic-bezier(0.390, 0.575, 0.565, 1.000)',
			easeInOutSine: 'cubic-bezier(0.445, 0.050, 0.550, 0.950)'
		};
	
		$.extend(jQuery.easing, {
			easeInOutSine: function (x, t, b, c, d) {
				return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
			},
			easeOutSine: function (x, t, b, c, d) {
				return c * Math.sin(t/d * (Math.PI/2)) + b;
			},
			easeOutCubic: function (x, t, b, c, d) {
				return c*((t=t/d-1)*t*t + 1) + b;
			}
		});
	
		$.thePhotoSlider = function(element, settings) {
			var self = $(element).data("thePhotoSlider");
	
			if (!self) {
				this._init(element, settings);
			}
			else {
				self.update();
			};
		};
	
		$.thePhotoSlider.defaults = {
			mode: {
				type: "slider"
			},
			responsive: true,
			height: false,
			width: false,
			sidePaddings: 0,
			storeHTML: false,
			autoPlay: false,
			threshold: 20,
			resizeImg: false,
			imageScaleMode:"none",
			imageAlignCenter:false,
			collapsePoint: 700,
			transformEnable: true,
			calcAutoHeight :false,
			columBasedResize: false,
			resizeHeight: false
		};
	
		$.thePhotoSlider.prototype = {
			_init: function(element, settings) {
				var self = this;
				self.st = $.extend({}, $.thePhotoSlider.defaults, settings);
				self.ev = $(self);
	
				self.autoPlay = {
					enabled: false,
					delay: 2000,
					loop: true
				};
	
				self.currSlide = 0;
				self.noSlide = true;
				self.lockLeft = true;
				self.lockRight = true;
	
				self.sliderLock = false;
				self.lockTimeout = false;
	
				self.wrap = {};
				self.wrap.$el = $(element);
				self.wrap.width = 0;
				self.wrap.height = false;
				self.wrap.$el.data("thePhotoSlider", self);
	
				self.viewport = self.wrap.$el.find(".ts-viewport");
	
				self.cont = {};
				self.cont.$el = self.viewport.find(".ts-cont");
				self.cont.width = 0;
				self.cont.startX = 0;
				self.cont.instantX = 0;
		 
				self.slides = {};
				self.slides.$items = self.cont.$el.children();
				self.slides.number = self.slides.$items.length;
				self.slides.position = [];
				self.slides.width = [];
				self.slides.isLoaded = [];
	
				self.drag = {};
				self.drag.isMoving = false;
				self.drag.startX = 0;
				self.drag.startY = 0;
				self.drag.offsetX = 0;
				self.drag.offsetY = 0;
				self.drag.lockX = false;
				self.drag.lockY = false;
	
				self.features = {};
				self._featureDetection();
	
				if (self.st.storeHTML) self.origHTML = self.wrap.$el.html();
				self._buildHTML();
	
				self._calcSliderSize();
				self._resizeImage();
				if (!self.wrap.height) self.wrap.$el.addClass("ts-autoHeight");
	
				self._setSliderWidth();
				self._adjustSlides();
				self._setSliderHeight();
	
				/* if (self.st.mode.type === "centered") */ self.slideTo(0, true);
	
				if (!self.noSlide) self._bindEvents();
	
				setTimeout(function() {
					self.wrap.$el.addClass("ts-ready");
					self.ev.trigger("sliderReady");
				}, 20);
	
				if (self.st.responsive) {
					if (!("onorientationchange" in window)) {
						var dtResizeTimeout;
	
						$(window).on("resize", function(e) {
							clearTimeout(dtResizeTimeout);
							dtResizeTimeout = setTimeout(function() {
								self.update();
							}, 200);
						});
					}
					else {
						var scrOrientation = window.orientation;
	
						$(window).on("orientationchange", function(e) {
							var tempOrientation = window.orientation;
	
							if (tempOrientation !== scrOrientation) {
								scrOrientation = tempOrientation;
								self.update();
							};
						});
					};
				};
	
				if(self.st.autoPlay.enabled) {
					self.play();
				};
			},
	
			_featureDetection: function() {
				var self = this,
					tempStyle = document.createElement('div').style,
					vendors = ['webkit','Moz','ms','O'],
					tempV;
					self.features.vendor = '';
	
	
				for (i = 0; i < vendors.length; i++ ) {
					tempV = vendors[i];
					if (!self.features.vendor && (tempV + 'Transform') in tempStyle ) {
						self.features.vendor = "-"+tempV.toLowerCase()+"-";
					}
				}
				
				if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1 && !('ontouchstart' in window)) {
					self.features.css3d = Modernizr.csstransforms3d;
					//self.features.css3d = false;
				}
				else if (typeof Modernizr != "undefined") {
					self.features.css3d = Modernizr.csstransforms3d;
					//self.features.css3d = false;
				}
			
			},
	
			_buildHTML: function() {
				var self = this;
	
				if (self.st.mode.type === "centered") {
					self.wrap.$el.addClass("ts-centered");
				};
	
				if (self.st.mode.type === "slider") {
					self.slides.$items.addClass("ts-slide");
				}
				else if (self.st.mode.type === "scroller" || self.st.mode.type === "centered" || self.st.mode.type === "carousel") {
					self.slides.$items.addClass("ts-cell");
				};
			},
	
			_calcSliderSize: function() {
				var self = this,
					typeofWidth = typeof self.st.width,
					typeofHeight = typeof self.st.height,
					tempWidth = false,
					tempHeight = false;
	
				self.wrap.width = self.wrap.$el.width();
	
				if (typeofWidth === "function") {
					tempWidth = self.st.width(this);
				}
				else if (typeofWidth === "number") {
					tempWidth = self.st.width;
				};
	
				if (typeofHeight === "function") {
					tempHeight = self.st.height(this);
				}
				else if (typeofHeight === "number") {
					tempHeight = self.st.height;
				};
	
				if (tempHeight && !tempWidth) { 
					// Calculate once or on resize (if typeofHeight === "function")
					self.wrap.height = tempHeight;
				}
				else if (tempHeight && tempWidth) {
					// Calculate on resize
					self.wrap.height = ( tempHeight * self.wrap.width ) / tempWidth;
				}
				else {
					// Calculate on every slide change and resize
					self.wrap.height = false;
				};
			},
	
			_resizeImage:function() {
	
				var self = this;
				var $slide = $(self.slides.$items[i]);
	
				if (self.st.resizeImg === true) {
					self.cont.width = 0;
					self.slides.$items.each(function(i) {
						var $slide = $(self.slides.$items[i]),
							tempCSS = {};
						var img = $slide.find("img");
						var classToFind = 'rsMainSlideImage';
						var isVideo;
						var imgAlignCenter = self.st.imageAlignCenter,
							imgScaleMode = self.st.imageScaleMode,
							tempEl;
	
						if(!img) {
							return;
						}
	
						var baseImageWidth = parseInt(img.attr("width")),
							baseImageHeight = parseInt(img.attr("height"));
	
	
						//slideObject.isRendered = true;
						if(imgScaleMode === 'none') {
							return;
						}
						var containerWidth = self.wrap.width,
							containerHeight = self.wrap.height,
							hRatio,
							vRatio,
							ratio,
							nWidth,
							nHeight,
							cssObj = {};
	
						if(imgScaleMode === 'fit-if-smaller') {
							if(baseImageWidth > containerWidth || baseImageHeight > containerHeight) {
								imgScaleMode = 'fit';
							}
						}
						if(imgScaleMode === 'fill' || imgScaleMode === 'fit') {   
							hRatio = containerWidth / baseImageWidth;
							vRatio = containerHeight / baseImageHeight;
	
							if (imgScaleMode  == "fill") {
								ratio = hRatio > vRatio ? hRatio : vRatio;                          
							} else if (imgScaleMode  == "fit") {
								ratio = hRatio < vRatio ? hRatio : vRatio;                    
							} else {
								ratio = 1;
							}
	
							nWidth = Math.ceil(baseImageWidth * ratio, 10);
							nHeight = Math.ceil(baseImageHeight * ratio, 10);
						} else {                
							nWidth = baseImageWidth;
							nHeight = baseImageHeight;    
	
						}
						if(imgScaleMode !== 'none') {
							cssObj.width = nWidth;
							cssObj.height = nHeight;
	
						}
						if (imgAlignCenter) { 
							cssObj.marginLeft = Math.floor((containerWidth - nWidth) / 2);
							cssObj.marginTop = Math.floor((containerHeight - nHeight) / 2);
						}
						img.css(cssObj);
					})
				}
			},
	
			_setSliderWidth: function() {
				var self = this;
	
				if (self.st.mode.type !== "centered") {
					self.viewport.css({
						width: self.wrap.width
					});
				}
				else if (self.wrap.width > self.st.collapsePoint) {
					self.wrap.$el.removeClass("ts-collapsed");
				}
				else {
					self.wrap.$el.addClass("ts-collapsed");
				};
			},
	
			_setSliderHeight: function() {
				var self = this;
	
				if (typeof self.wrap.height === "number") {
					// Fixed & proportional height
					self.viewport.css({
						height: self.wrap.height
					});
				}
				else if (self.st.mode.type === "scroller" || self.st.mode.type === "centered" || self.st.mode.type === "carousel") {
					// Auto height; scroller and centered only
					//Aply responsive height 
					if(self.st.resizeHeight){
						var articleHeights = $(self.viewport).find("article").map(function() {
						    return $(this).height();
						}).get();
	
						// Math.max takes a variable number of arguments
						// `apply` is equivalent to passing each height as an argument
						var maxHeight = Math.max.apply(null, articleHeights);
						self.viewport.css({
							height: maxHeight
						});
						$(self.slides.$items).css({
							height: maxHeight
						});
					}
					if (self.viewport.css("height") === "0px" || self.viewport.css("height") == 0 || !self.viewport.css("height")) {
						self.viewport.css({
							height: Math.max.apply(null, self.slides.height)
						});
					};
				}
				else if (self.slides.isLoaded[self.currSlide]) {
					// Auto height; current slide is loaded
					var jsHeight = $(self.slides.$items[self.currSlide]).height();
	
					if (jsHeight > 0) {
						self.viewport.css({
							height: jsHeight
						});
					}
					else {
						// !This will cause "collapsed" slider
						self.viewport.css({
							height: "auto"
						});
					};
				}
				else {
					// Auto height; current slide is NOT loaded
					var jsHeight = $(self.slides.$items[self.currSlide]).height();
	
					if (jsHeight > 0) {
						self.viewport.css({
							height: jsHeight
						});
					}
					else {
						// !This will cause "collapsed" slider
						self.viewport.css({
							height: auto
						});
					};
				};
			},
	
			_adjustSlides: function() {
				var self = this;
	
				if (self.st.mode.type === "slider") {
					self.cont.width = 0;
	
					self.slides.$items.each(function(i) {
						var $slide = $(self.slides.$items[i]),
							tempCSS = {};
						
						self.slides.position[i] = - self.cont.width - self.st.sidePaddings/2;
						self.cont.width = self.cont.width + self.wrap.width + self.st.sidePaddings;
						//if (self.wrap.height) tempCSS.height = self.wrap.height;
						tempCSS.left = -self.slides.position[i];
	
						if (!self.slides.isLoaded[i]) {
							$slide.find("img").loaded(false, function() {
								self.slides.isLoaded[i] = true;
								$slide.addClass("ts-loaded");
							}, true);
						} else {
						};
	
						$slide.css(tempCSS);
					});
				}
				else if (self.st.mode.type === "centered") {
						self.cont.width = 0;
						self.slides.contRatio = [];
						self.slides.ratio = [];
	
					if (self.st.mode.lsMinW || self.st.mode.lsMaxW) {
						var lsMinW = self.wrap.width/100 * self.st.mode.lsMinW,
							lsMaxW = self.wrap.width/100 * self.st.mode.lsMaxW;
					};
	
					if (self.st.mode.ptMinW || self.st.mode.ptMaxW) {
						var ptMinW = self.wrap.width/100 * self.st.mode.ptMinW,
							ptMaxW = self.wrap.width/100 * self.st.mode.ptMaxW;
					};
	
					self.slides.$items.each(function(i) {
						var $slide = $(self.slides.$items[i]),
							tempCSS = {};
	
						var dataWidth = $slide.attr("data-width") ? parseFloat($slide.attr("data-width")) : $slide.width(),
							dataHeight = $slide.attr("data-height") ? parseFloat($slide.attr("data-height")) : $slide.height();
						
	
						if (!self.slides.contRatio[i]) {
							self.slides.contRatio[i] =  dataWidth / dataHeight;
	
							if (self.slides.contRatio[i] > 1) {
								$slide.addClass("ts-ls");
							}
							else {
								$slide.addClass("ts-pt");
							};
						};
	
						if (self.wrap.width > self.st.collapsePoint) {
							dataHeight = self.wrap.height;
							dataWidth = self.wrap.height * self.slides.contRatio[i];
		
							if ((lsMinW || lsMaxW) && (dataWidth > dataHeight)) {
								if (lsMinW === lsMaxW || dataWidth > lsMaxW) {
									dataWidth = lsMaxW;
								}
								else if (dataWidth < lsMinW) {
									dataWidth = lsMinW;
								};
							}
							else if ((ptMinW || ptMaxW) && (dataWidth <= dataHeight)) {
								if (ptMinW === ptMaxW || dataWidth > ptMaxW) {
									dataWidth = ptMaxW;
								}
								else if (dataWidth < ptMinW) {
									dataWidth = ptMinW;
								};            
							};
		
							self.slides.ratio[i] = dataWidth / dataHeight;
	
							tempCSS.height = self.wrap.height;
							tempCSS.width = self.slides.width[i] = dataWidth;
		
							self.slides.position[i] = - self.cont.width;
							self.cont.width = self.cont.width + self.slides.width[i] + self.st.sidePaddings;
							tempCSS.left = -self.slides.position[i];
						}
						else {
							dataHeight = tempCSS.height = self.wrap.height;
							dataWidth = self.slides.width[i] = tempCSS.width = self.wrap.width;
							self.slides.ratio[i] = dataWidth / dataHeight;
	
							self.slides.position[i] = - self.cont.width;
							self.cont.width = self.cont.width + self.slides.width[i];
	
							tempCSS.left = -self.slides.position[i];
						};
	
						// Adjust position to slide center
						self.slides.position[i] = self.slides.position[i] - (self.slides.width[i]/2);
	
	
						if (self.slides.ratio[i] > self.slides.contRatio[i]) {
							$slide.removeClass("ts-narrow");
							$slide.addClass("ts-wide");
						}
						else {
							$slide.removeClass("ts-wide");
							$slide.addClass("ts-narrow");
						};
	
						if (!self.slides.isLoaded[i]) {
							$slide.find("img").loaded(false, function() {
								self.slides.isLoaded[i] = true;
								$slide.addClass("ts-loaded");
							}, true);
						} 
						else {
						};
	
						$slide.css(tempCSS);
	
					});
				}
				else if (self.st.mode.type === "scroller") {
					self.cont.width = 0;
					self.slides.ratio = [];
					if (!(typeof self.wrap.height === "number")) {
						self.slides.height = [];
					}
					//determine if max-width has %
					if(typeof self.slides.$items.parents(".slider-wrapper").attr("data-max-width") != "undefined"){
						var dataMaxWidth = (self.slides.$items.parents(".slider-wrapper").width() * parseFloat(self.slides.$items.parents(".slider-wrapper").attr("data-max-width")))/100;
						
					}
	
					self.slides.$items.each(function(i) {
						var $slide = $(self.slides.$items[i]),
							tempCSS = {};
	
						var dataWidth = $slide.attr("data-width") ? parseFloat($slide.attr("data-width")) : $slide.width(),
							dataHeight = $slide.attr("data-height") ? parseFloat($slide.attr("data-height")) : $slide.height();
						
						if(dataWidth > dataMaxWidth){
							var dataWidth = dataMaxWidth;
						}
		
						if (dataWidth > 0 && dataHeight > 0) {
							self.slides.ratio[i] =  dataWidth / dataHeight;
						}
						else {
							self.slides.ratio[i] = 1;
						};
	
		
						if (typeof self.wrap.height === "number") {
							// Fixed & proportional height
							self.slides.width[i] = self.wrap.height * self.slides.ratio[i];
		
							tempCSS.width = self.slides.width[i];
							tempCSS.height = self.slides.width[i] / self.slides.ratio[i]; 
						}
						else if (dataWidth > 0 && dataHeight > 0) {
							// Auto height;
							if (!self.slides.width[i]) tempCSS.width = self.slides.width[i] = dataWidth;
							if (!self.slides.height[i] && !self.st.resizeHeight) { 
								tempCSS.height = "100%";
							};
							self.slides.height[i] = dataHeight;
						}
						else {
							// Auto height;
							$slide.css("height", "auto");
	
							self.slides.width[i] = $slide.width();
							self.slides.height[i] = $slide.height();
	
							tempCSS.height = "100%";
						};
						if(self.st.columBasedResize) {
							self.slides.width[i] = $slide.width();
							
						}
						self.slides.position[i] = - self.cont.width;
						self.cont.width = self.cont.width + self.slides.width[i];
						if (i < self.slides.number - 1) self.cont.width += self.st.sidePaddings
						tempCSS.left = -self.slides.position[i] //+ self.st.sidePaddings/2;
						
		
						if (!self.slides.isLoaded[i]) {
							$slide.find("img").loaded(false, function() {
								self.slides.isLoaded[i] = true;
								$slide.addClass("ts-loaded");
							}, true);
						}
						else {
						};
		
						$slide.css(tempCSS);
					});
				}
				else if (self.st.mode.type === "carousel") {
					self.cont.width = 0;
	
					var perView =  self.st.mode.perView,
						minWidth = self.st.mode.minWidth,
						cellWidth = self.wrap.width/perView;
			
					while (cellWidth < minWidth && perView > 0.31) {
						perView--;
						if (perView < 1) perView = 1;
						cellWidth = self.wrap.width/perView;
					};
	
					self.perView = perView;
					//self.st.sidePaddings = 0;
			
					self.slides.$items.each(function(i) {
						var $slide = $(self.slides.$items[i]),
							tempCSS = {};
	
						self.slides.position[i] = - self.cont.width;
						self.cont.width = self.cont.width + cellWidth;
						tempCSS.width = cellWidth - self.st.sidePaddings;
						tempCSS.left = -self.slides.position[i] + self.st.sidePaddings/2;
	
						$slide.css(tempCSS);
					});
				};
	
				// Adjusting slides conteiner position and updating navigation
				if ( (self.st.mode.type !== "centered") && (self.cont.width <= self.wrap.width) ) {
					self.noSlide = true;
					self._transitionStart(0, 0, "easeInOutSine", true);
					self.cont.$el.css( "left", (self.wrap.width - self.cont.width) / 2 );
	
					self.lockLeft = true;
					self.lockRight = true;
					self.ev.trigger("updateNav");
				}
				else if ( (self.st.mode.type === "centered") && (self.slides.number < 2) /* && (self.cont.width <= self.wrap.width / 2) */ ) {
					self.noSlide = true;
					self._transitionStart(0, 0, "easeInOutSine", true);
					self.cont.$el.css( "left", -(self.cont.width) / 2 );
	
					self.lockLeft = true;
					self.lockRight = true;
					self.ev.trigger("updateNav");
				}
				else {
					self.noSlide = false;
					self.cont.$el.css( "left", "" );
	
					if (self.lockRight) {
						self.lockLeft = false;
						self.lockRight = true;
						self.ev.trigger("lockRight").trigger("updateNav");
					}
					else if ( self.currSlide <= 0 ) {
						self.lockLeft = true;
						self.lockRight = false;
						self.ev.trigger("lockLeft").trigger("updateNav");
					}
					else if ( self.currSlide > 0 ) {
						self.lockLeft = false;
						self.lockRight = false;
						self.ev.trigger("updateNav");
					};
				};
			},
	
			_unifiedEvent: function(event) {
				if (event.originalEvent.touches !== undefined && event.originalEvent.touches[0]) {
					event.pageX = event.originalEvent.touches[0].pageX;
					event.pageY = event.originalEvent.touches[0].pageY;
				}
				return event;
			},
	
			_unifiedX: function() {
				var self = this,
					coord = 0,
					css3dTransform = self.cont.$el.css("transform");
	
				if (css3dTransform) {
					var css3dArray = css3dTransform.split(", ");
				}
	
				if (self.features.css3d && css3dTransform !== "none" && css3dArray[0] === "matrix(1") {
					coord = parseFloat(css3dArray[4]);
				}
				else if (self.features.css3d && css3dTransform !== "none" && css3dArray[0] === "matrix3d(1") {
					coord = parseFloat(css3dArray[12]);
				}
				else {
					coord = parseFloat(self.cont.$el.css("left"));
				};
	
				return coord;
			},
	
			_bindEvents: function() {
				var self = this;
				if(self.st.transformEnable){
					self.wrap.$el.on("mousedown.theSlider touchstart.theSlider", function(event) {
						if (event.type != "touchstart") event.preventDefault();
	
						self._onStart( self._unifiedEvent(event) );
	
						$(document).on("mousemove.theSlider touchmove.theSlider", function(event) {
							self._onMove( self._unifiedEvent(event) );
						});
						$(document).on("mouseup.theSlider mouseleave.theSlider touchend.theSlider touchcancel.theSlider", function(event) {
							$(document).off("mousemove.theSlider mouseup.theSlider mouseleave.theSlider touchmove.theSlider touchend.theSlider touchcancel.theSlider");
							self._onStop( self._unifiedEvent(event) );
						});
					});
				}
			},
	
			_unbindEvents: function() {
				var self = this;
	
				self.wrap.$el.off("mousedown.theSlider touchstart.theSlider");
				$(document).off("mousemove.theSlider mouseup.theSlider mouseleave.theSlider touchmove.theSlider touchend.theSlider touchcancel.theSlider");
			},
	
			_onStart: function(event) {
				var self = this;
	
				if (!self.drag.isMoving && !self.sliderLock) {
					//self._transitionEnd();
	
					self.drag.isMoving = true;
					self.drag.startX = event.pageX;
					self.drag.startY = event.pageY;
					self.cont.startX = self._unifiedX();
	
					self.drag.offsetX = 0;
					self.drag.offsetY = 0;
					self.drag.lockX = false;
					self.drag.lockY = false;
				}
				else {
					//self._transitionCancel();
				};
			},
	
			_onMove: function(event) {
				var self = this,
					coord = 0;
					//self.pause();
				self.ev.trigger('psOnMove');
				if (self.drag.isMoving) {
					self.drag.offsetX = event.pageX - self.drag.startX;
					self.drag.offsetY = event.pageY - self.drag.startY;
	
					if ( (Math.abs(self.drag.offsetX) >= self.st.threshold-1) && (Math.abs(self.drag.offsetX) > Math.abs(self.drag.offsetY)) && !self.drag.lockX ) {
						self.drag.lockX = false;
						self.drag.lockY = true;
						if (event.type == "touchmove") self.drag.offsetY = 0;
					} 
					else if( (Math.abs(self.drag.offsetY) >= self.st.threshold-1) && (Math.abs(self.drag.offsetX) < Math.abs(self.drag.offsetY)) && !self.drag.lockY ) {
						self.drag.lockX = true;
						self.drag.lockY = false;
						if (event.type == "touchmove") self.drag.offsetX = 0;
					};
	
					if (self.drag.lockX && event.type == "touchmove") self.drag.offsetX = 0;
					else if (self.drag.lockY && event.type == "touchmove") self.drag.offsetY = 0;
	
					if (self.drag.lockY) event.preventDefault();
	
					self.cont.instantX = self.cont.startX + self.drag.offsetX;
	
					if ( self.cont.instantX < 0 && self.cont.instantX > -self.cont.width + self.viewport.width()) {
						coord = self.cont.instantX;
					}
					else if (self.cont.instantX >= 0) {
						coord = self.cont.instantX/4;
					}
					else {
						coord = (-self.cont.width + self.viewport.width()) + ((self.cont.width - self.viewport.width() + self.cont.instantX) / 4);
					};
	
					self._doDrag(coord);
				};
	
	
				if (self.st.autoPlay.enabled) {
					self.pause();
				};
			},
	
			_onStop: function(event) {
				var self = this;
				//self.pause()
				self.ev.trigger('psOnStop');
	
				if (self.drag.isMoving) {
					self.cont.instantX = self.cont.startX + self.drag.offsetX;
	
					if (Math.abs(self.drag.offsetX) > self.st.threshold) {
						self.wrap.$el.addClass("ts-interceptClicks");
						self.wrap.$el.one("click.preventClick", function(e) {
							e.preventDefault();
							e.stopImmediatePropagation();
							e.stopPropagation();
						}); 
						window.setTimeout(function() {
							self.wrap.$el.off('click.preventClick');
							self.wrap.$el.removeClass("ts-interceptClicks");
						}, 301);
					};
	
					self._autoAdjust();
					self._setSliderHeight();
	
					self.cont.startX = 0;
					self.cont.instantX = 0;
			
					self.drag.isMoving = false;
					self.drag.startX = 0;
					self.drag.startY = 0;
					self.drag.offsetX = 0;
					self.drag.offsetY = 0;
					self.drag.lockX = false;
					self.drag.lockY = false;
				};
	
				if(self.st.autoPlay.enabled) {
					self.play();
				}
	
				return false;
			},
	
			_doDrag: function(coord) {
				var self = this;
			//	self.pause();
				if(self.st.transformEnable){
					if (self.features.css3d) {
						var tempCSS = {};
	
						tempCSS[self.features.vendor+"transform"] = "translate3d("+coord+"px,0,0)";
						tempCSS["transform"] = "translate3d("+coord+"px,0,0)";
						tempCSS[self.vendor+"transition"] = "";
						tempCSS["transition"] = "";
	
						self.cont.$el.css(tempCSS);
					}
					else {
						self.cont.$el.css({
							"left": coord
						});
					};
				}
			},
	
			_calcCurrSlide: function(coord) {
				var self = this,
					tempCurrSlide = self.slides.number - 1;
	
				self.slides.$items.each(function(i) {
					if ( coord > self.slides.position[i] ) {
						tempCurrSlide = i-1;
						return false;
					};
				});
				if (tempCurrSlide < 0) tempCurrSlide = 0;
	
				return tempCurrSlide;
			},
	
			_isRightExceed: function(coord) {
				var self = this,
					edge = 0;
	
				if (self.st.mode.type === "centered") {
					edge = self.slides.position[self.slides.number - 1];
				}
				else {
					edge = -self.cont.width + self.viewport.width();
				};
	
				if (coord < edge) {
					return true;
				}
				else {
					return false;
				};
			},
	
			_autoAdjust: function() {
				var self = this,
					adjustTo = 0,
					duration = 0,
					tempCurrSlide = self.slides.number - 1;
	
				if (self.cont.instantX >= 0) {
					// leftmost edge reached
					adjustTo = self.slides.position[0];
					self.currSlide = 0;
	
					self.lockLeft = true;
					self.lockRight = false;
					self.ev.trigger("lockLeft").trigger("updateNav");
				}
				else if ( self._isRightExceed(self.cont.instantX) ) {
					// rightmost edge reached
					if (self.st.mode.type === "centered") {
						adjustTo = self.slides.position[self.slides.number-1];
					}
					else {
						adjustTo = -self.cont.width + self.viewport.width();
					};
	
					self.currSlide = self._calcCurrSlide(adjustTo);
	
					self.lockLeft = false;
					self.lockRight = true;
					self.ev.trigger("lockRight").trigger("updateNav");
				}
				else {
					// autoadjust to closest slide
					if (self.drag.offsetX < -self.st.threshold) {
						// flick from right to left
						tempCurrSlide = self._calcCurrSlide(self.cont.instantX) + 1;
	
						if (self._isRightExceed(self.slides.position[tempCurrSlide])) {
							adjustTo = -self.cont.width + self.viewport.width();
	
							for ( i = tempCurrSlide; i >= 0; i-- ) {
								if (!self._isRightExceed(self.slides.position[i])) {
									tempCurrSlide = i;
									break;
								}
							}
	
							self.lockLeft = false;
							self.lockRight = true;
							self.ev.trigger("lockRight").trigger("updateNav");
						}
						else {
							adjustTo = self.slides.position[tempCurrSlide];
	
							if  ( tempCurrSlide < self.slides.number - 1 ) {
								self.lockLeft = false;
								self.lockRight = false;
								self.ev.trigger("updateNav");
							}
							else {
								self.lockLeft = false;
								self.lockRight = true;
								self.ev.trigger("lockRight").trigger("updateNav");
							};
						};
	
						self.currSlide = tempCurrSlide;
					}
					else if (self.drag.offsetX > self.st.threshold) {
						// flick from left to right
						self.currSlide = self._calcCurrSlide(self.cont.instantX);
						adjustTo = self.slides.position[self.currSlide];
	
						if ( self.currSlide > 0 ) {
							self.lockLeft = false;
							self.lockRight = false;
							self.ev.trigger("updateNav");
						}
						else {
							self.lockLeft = true;
							self.lockRight = false;
							self.ev.trigger("lockLeft").trigger("updateNav");
						};
					}
					else {
						// flick cenceled, since it's to short
						adjustTo = self.cont.startX;
					};
	
				};
	
				duration = Math.sqrt(Math.abs(self.cont.instantX - adjustTo)) * 10 + 100;
				self._transitionStart(adjustTo, duration, "easeOutSine");
			},
	
			_transitionStart: function(coord, duration, easing, justSet) {
				var self = this,
					tempCSS = {},
					cssEasing = $.rsCSS3Easing[easing];
	
				self._transitionEnd();
				self.ev.trigger("beforeTransition");
	
				if (justSet) {
					if(self.st.transformEnable){
						if (self.features.css3d) {
							tempCSS[self.features.vendor+"transform"] = "translate3d("+coord+"px,0,0)";
							tempCSS["transform"] = "translate3d("+coord+"px,0,0)";
						}
						else {
							//console.log("and, here's the issue");
							tempCSS.left = coord;
						};
					}
	
					self.cont.$el.css(tempCSS);
					return false;
				}
	
				self.ev.trigger("beforeTransition");
	
				self.sliderLock = true;
				clearTimeout(self.lockTimeout);
				self.lockTimeout = setTimeout(function() {
					self.sliderLock = false;
					self.ev.trigger("afterTransition");
				}, duration);
				if(self.st.transformEnable){
					if (self.features.css3d) {
						tempCSS[self.features.vendor+"transform"] = "translate3d("+coord+"px,0,0)";
						tempCSS["transform"] = "translate3d("+coord+"px,0,0)";
						tempCSS[self.features.vendor+"transition"] = "all "+duration+"ms "+cssEasing;
						tempCSS["transition"] = "all "+duration+"ms "+cssEasing;
	
						self.cont.$el.css(tempCSS);
					}
					else {
						self.cont.$el.animate({
							"left": coord
						}, duration, easing);
					};
				}
			},
	
			_transitionEnd: function() {
				var self = this;
				self.ev.trigger('psTransitionEnd');
				if(self.st.transformEnable){
					if (self.features.css3d) {
						var tempCSS = {};
							tempCSS[self.vendor+"transition"] = "";
							tempCSS["transition"] = "";
	
						self.cont.$el.css(tempCSS);
					}
					else {
						self.cont.$el.stop();
					};
				}
			},
	
			_transitionCancel: function() {
				var self = this,
					coord = self.cont.$el.position().left,
					tempCSS = {};
	
				tempCSS[self.vendor+"transition"] = "";
				tempCSS["transition"] = "";
	
				self.cont.$el.off("webkitTransitionEnd otransitionend oTransitionEnd msTransitionEnd transitionend");
				if(self.st.transformEnable){
					if (self.features.css3d) {
						var str = self.cont.$el.css("transform"),
							result = str.split(", ");
	
						coord = result[4];
	
						tempCSS[self.features.vendor+"transform"] = "translate3d("+coord+"px,0,0)";
						tempCSS["transform"] = "translate3d("+coord+"px,0,0)";
	
						self.cont.$el.css(tempCSS);
					}
					else {
						self.cont.$el.stop();
						self.cont.$el.animate({
							"left": coord
						}, duration, easing);
					};
				}
			},
	
			pause: function() {
				var self = this;
				self.ev.trigger('autoPlayPause');
				self._autoPlayRunning = false;
				if( self._autoPlayTimeout) {
					clearTimeout(self._autoPlayTimeout);
					self._autoPlayTimeout = null;
				}
				
			},
	
			slideTo: function(slideID, justSet) {
				var self = this,
					slideToX = self.slides.position[slideID],
					duration = 0,
					oldID = self.currSlide;
				 self.pause();
				self.ev.trigger('psBeforeAnimStart');
	
				if (self.noSlide) return false;
	
				self._transitionEnd();
	
				if (slideToX >= self.slides.position[0]) {
					// leftmost edge reached
					self.currSlide = 0;
	
					self.lockLeft = true;
					self.lockRight = false;
					self.ev.trigger("lockLeft").trigger("updateNav");
				}
				else if ( self._isRightExceed(slideToX) || slideID >= self.slides.number - 1 ) {
					// rightmost edge reached
					if (self.st.mode.type === "centered") {
						slideToX = self.slides.position[slideID];
						self.currSlide = slideID;
					}
					else {
						slideToX = -self.cont.width + self.viewport.width();
						self.currSlide = self._calcCurrSlide(slideToX);
					};
	
					self.lockLeft = false;
					self.lockRight = true;
					self.ev.trigger("lockRight").trigger("updateNav");
				}
				else {
					self.currSlide = slideID;
	
					self.lockLeft = false;
					self.lockRight = false;
					self.ev.trigger("updateNav");
				};
	
				//duration = Math.abs(self.slides.position[oldID] - slideToX)/2 + 100;
				duration = Math.sqrt(Math.abs(self.slides.position[oldID] - slideToX)) * 10 + 100;
				self._transitionStart(slideToX, duration, "easeInOutSine", justSet);
	
				if ( /*$(".auto-play-btn").hasClass('paused')*/self.st.autoPlay.enabled) {
					self.play();
				}
				if(self.st.calcAutoHeight){
					self._setSliderHeight();
				}
			},
			// stopAutoPlay: function() {
			// 	var self = this;
			// 	self._autoPlayPaused = self._autoPlayEnabled = false;
			// 	self.pause();
			// },
			startPlay: function() {
				var self = this;
				self.ev.trigger('autoPlayPlay');
				if (self.currSlide + 1 <= self.slides.number - 1 && !self.lockRight) {
					self.slideTo(self.currSlide + 1);
				}
				else if (self.currSlide >= self.slides.number-1 && self.st.autoPlay.loop) {
					self.slideTo(0);
				}
				else if (self.lockRight && self.st.autoPlay.loop) {
					self.slideTo(0);
				}
	
			},
	
			play: function() {
				var self = this;
				self.ev.trigger('autoPlayPlay');
				self._autoPlayRunning = true;
				if(self._autoPlayTimeout) {
					clearTimeout(self._autoPlayTimeout);
				}
				self._autoPlayTimeout = setTimeout( function() {
					self.startPlay();
				}, self.st.autoPlay.delay );
			},
	
			slideNext: function() {
				var self = this;
	
				if (self.currSlide + 1 <= self.slides.number - 1) {
					self.slideTo(self.currSlide + 1);
				}
				else {
					return false;
				};
			},
	
			slidePrev: function() {
				var self = this;
	
				if (self.currSlide - 1 >= 0) {
					self.slideTo(self.currSlide - 1);
				}
				else if (self.currSlide == 0 && self.lockLeft == false) {
					self.slideTo(self.currSlide);
				}
				else {
					return false;
				};
			},
	
			update: function() {
				var self = this;
	
				self._calcSliderSize();
				self._resizeImage();
				self._setSliderWidth();
				self._adjustSlides();
				self._setSliderHeight();
				self._doDrag();
	
				if (self.noSlide) {
					self.slideTo(0, true);
					self._unbindEvents();
				}
				else {
					self.slideTo(self.currSlide, true);
					self._bindEvents();
				}
			}
		};
	
		$.fn.thePhotoSlider = function(settings) {
			return this.each(function() {
				new $.thePhotoSlider(this, settings);
			});
		};
	
	//})(jQuery);

	
	
	// https://github.com/callmecavs/layzr.js
	// CONSTRUCTOR
	/* Lazy loading: end */
	function Layzr(options) {
	  // debounce
	  this._lastScroll = 0;
	  this._ticking    = false;
	
	  // options
	  options = options || {};
	
	  this._optionsContainer  = document.querySelector(options.container) || window;
	  this._optionsSelector = options.selector || '[data-layzr]';
	  this._optionsAttr   = options.attr || 'data-layzr';
	  this._optionsAttrSrcSet = options.attrSrcSet || 'data-layzr-srcset';
	  this._optionsAttrRetina = options.retinaAttr || 'data-layzr-retina';
	  this._optionsAttrBg   = options.bgAttr || 'data-layzr-bg';
	  this._optionsAttrHidden = options.hiddenAttr || 'data-layzr-hidden';
	  this._optionsThreshold  = options.threshold || 0;
	  this._optionsBefore   = options.before || null;
	  this._optionsAfter    = options.after || null;  
	  this._optionsCallback = options.callback || null;
	
	  // properties
	  this._retina  = window.devicePixelRatio > 1;
	  this._srcAttr = this._retina ? this._optionsAttrRetina : this._optionsAttr;
	
	  // nodelist
	  this._nodes = document.querySelectorAll(this._optionsSelector);
	
	  // scroll and resize handler
	  this._handlerBind = this._requestScroll.bind(this);
	
	  // call to create
	  this._create();
	  
	}
	
	// DEBOUNCE HELPERS
	// adapted from: http://www.html5rocks.com/en/tutorials/speed/animations/
	
	Layzr.prototype._requestScroll = function() {
	  if(this._optionsContainer === window) {
	    this._lastScroll = window.pageYOffset;
	  }
	  else {
	    this._lastScroll = this._optionsContainer.scrollTop + this._getOffset(this._optionsContainer);
	  }
	
	  this._requestTick();
	};
	
	Layzr.prototype._requestTick = function() {
	  if(!this._ticking) {
	    requestAnimationFrame(this.update.bind(this));
	    this._ticking = true;
	  }
	};
	
	// OFFSET HELPER
	// remember, getBoundingClientRect is relative to the viewport
	
	Layzr.prototype._getOffset = function(node) {
	  return node.getBoundingClientRect().top + window.pageYOffset;
	};
	
	// HEIGHT HELPER
	
	Layzr.prototype._getContainerHeight = function() {
	  return this._optionsContainer.innerHeight
	      || this._optionsContainer.offsetHeight;
	
	}
	
	// LAYZR METHODS
	
	Layzr.prototype._create = function() {
	  // fire scroll event once
	  this._handlerBind();
	
	  // bind scroll and resize event
	  this._optionsContainer.addEventListener('scroll', this._handlerBind, false);
	  this._optionsContainer.addEventListener('resize', this._handlerBind, false);
	};
	
	Layzr.prototype._destroy = function() {
	  // unbind scroll and resize event
	  this._optionsContainer.removeEventListener('scroll', this._handlerBind, false);
	  this._optionsContainer.removeEventListener('resize', this._handlerBind, false);
	};
	
	Layzr.prototype._inViewport = function(node) {
	  // get viewport top and bottom offset
	  var viewportTop = this._lastScroll;
	  var viewportBottom = viewportTop + this._getContainerHeight();
	
	  // get node top and bottom offset
	  var nodeTop = this._getOffset(node);
	  var nodeBottom = nodeTop + this._getContainerHeight();
	
	  // calculate threshold, convert percentage to pixel value
	  var threshold = (this._optionsThreshold / 100) * window.innerHeight;
	
	  // return if node in viewport
	  return nodeBottom >= viewportTop - threshold
	      && nodeTop <= viewportBottom + threshold
	      && !node.hasAttribute(this._optionsAttrHidden);
	  //console.log(this, node + "viewport")
	};
	
	Layzr.prototype._reveal = function(node) {
	  // get node source
	  var source = node.getAttribute(this._srcAttr) || node.getAttribute(this._optionsAttr),
	    lazyrObj = this;
	   // console.log( lazyrObj)
	
	  // call the callback
	  if(typeof this._optionsCallback === 'function') {
	    // "this" will be the node in the callback
	    node.addEventListener("load", function() {
	      lazyrObj._optionsCallback.call(node);
	    });
	  }
	
	  // before hook
	  if(typeof this._optionsBefore === 'function') {
	    // "this" will be the node in the callback
	    this._optionsBefore.call(node);
	  }
	
	  // set node src or bg image
	  if(node.hasAttribute(this._optionsAttrBg)) {
	    node.style.backgroundImage = 'url(' + source + ')';
	  }
	  else {
	    if (source) node.setAttribute('src', source);
	    if (node.hasAttribute(this._optionsAttrSrcSet)) node.setAttribute('srcset', node.getAttribute(this._optionsAttrSrcSet));
	  }
	
	  // after hook
	  if(typeof this._optionsAfter === 'function') {
	    // "this" will be the node in the callback
	    this._optionsAfter.call(node);
	  }
	
	  // remove node data attributes
	  node.removeAttribute(this._optionsAttr);
	  node.removeAttribute(this._optionsAttrSrcSet);
	  node.removeAttribute(this._optionsAttrRetina);
	  node.removeAttribute(this._optionsAttrBg);
	  node.removeAttribute(this._optionsAttrHidden);
	};
	
	Layzr.prototype.updateSelector = function() {
	  // update cached list of nodes matching selector
	  this._nodes = document.querySelectorAll(this._optionsSelector);
	};
	
	Layzr.prototype.update = function() {
	  // cache nodelist length
	  var nodesLength = this._nodes.length;
	
	  // loop through nodes
	  for(var i = 0; i < nodesLength; i++) {
	    // cache node
	    var node = this._nodes[i];
	
	    // check if node has mandatory attribute
	    if(node.hasAttribute(this._optionsAttr) || node.hasAttribute(this._optionsAttrSrcSet) || node.hasAttribute(this._optionsAttrRetina)) {
	      // check if node in viewport
	      if(this._inViewport(node)) {
	        // reveal node
	        this._reveal(node);
	        //console.log(node)
	      }
	    }
	  }
	
	  // allow for more animation frames
	  this._ticking = false;
	};
	
	/* Lazy loading: end */
	
	
	
	//Cache variables
	var $document = $(document),
		$window = $(window),
		$html = $("html"),
		$body = $("body"),
		$page = $("#page");

	/*!- Custom resize function*/
	var dtResizeTimeout;
	
	/* #Retina images using srcset polyfill
	================================================== */
	//Layzy img loading
	$.fn.layzrInitialisation = function(container) {
	  return this.each(function() {
	      var $this = $(this);

	      var layzr = new Layzr({
	        container: container,
	        selector: '.lazy-load',
	        attr: 'data-src',
	        attrSrcSet: 'data-srcset',
	        retinaAttr: 'data-src-retina',
	        hiddenAttr: 'data-src-hidden',
	        threshold: 30,
	        before: function() {
	          // For fixed-size images with srcset; or have to be updated on window resize.
	          this.setAttribute("sizes", this.width+"px");
			   var ext = $(this).attr("data-src").substring($(this).attr("data-src").lastIndexOf(".")+1);
			   if(ext == "png"){
			      $(this).parent().addClass("layzr-bg-transparent");
			  }
	        },
	        callback: function() {

	          	this.classList.add("is-loaded");
	         	var $this =  $(this);
	         	setTimeout(function(){
					$this.parent().removeClass("layzr-bg");
				}, 350)
	        }
	      });
	    });
	};
	$(".layzr-loading-on").layzrInitialisation();


/* #Check if element exists
================================================== */
	$.fn.exists = function() {
		if ($(this).length > 0) {
			return true;
		} else {
			return false;
		}
	}

	/* !- Check if element is loaded */
	$.fn.loaded = function(callback, jointCallback, ensureCallback){
		var len	= this.length;
		if (len > 0) {
			return this.each(function() {
				var	el		= this,
					$el		= $(el),
					blank	= "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";

				$el.on("load.dt", function(event) {
					$(this).off("load.dt");
					if (typeof callback == "function") {
						callback.call(this);
					}
					if (--len <= 0 && (typeof jointCallback == "function")){
						jointCallback.call(this);
					}
				});

				if (!el.complete || el.complete === undefined) {
					el.src = el.src;
				} else {
					$el.trigger("load.dt")
				}
			});
		} else if (ensureCallback) {
			if (typeof jointCallback == "function") {
				jointCallback.call(this);
			}
			return this;
		}
	};
	
	// !- Initialise slider
	$.fn.initSlider = function() {
		return this.each(function() {
		
			var $_this = $(this),
				attrW = $_this.data('width'),
				attrH = $_this.data('height');

			if ($_this.hasClass("royalReady")) {
				return;
			}

			$_this.postTypeScroller();

			$_this.addClass("royalReady");
			
		});
	};

})(jQuery);

