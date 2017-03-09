(function( factory ) {
	if ( typeof define === "function" && define.amd ) {

		// AMD. Register as an anonymous module.
		define([ "jquery" ], factory );
	} else {

		// Browser globals
		factory( jQuery );
	}
}(function( $ ) {
	
	var T5Admin = {};
	
	$(document).ready(function(){
        $(document).trigger('fly.ready');
    });
	
	/* ========================================================================
	* Fly NAVIGATION v1.5
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	* Remove it if you don't use the Main Navigation.
	* ========================================================================
	*
	* REQUIRE BOOTSTRAP POPOVER
	* http://getbootstrap.com/javascript/#popovers
	*
	* REQUIRE JQUERY RESIZEEND
	* https://github.com/nielse63/jQuery-ResizeEnd
	*
	* ========================================================================*/
	
	T5Admin.navgation = function(){
		
	    var $menulink               = null,
	        flyContainer          = null,
	        boxedContainer          = null,
	        flyMainNav            = null,
	        mainNavHeight           = null,
	        scrollbar               = null,
	        updateMethod            = false,
	        isSmallNav              = false,
	        screenSize              = null,
	        screenCat               = null,
	        defaultSize             = null,
	        flyWindow             = $(window),
	        ignoreAffix             = false,
	
	
	        // Determine and bind hover or "touch" event
	        // ===============================================
	        bindSmallNav = function(){
	            var hidePopover, $menulink = $('#mainnav-menu > li > a, #mainnav-menu-wrap .mainnav-widget a[data-toggle="menu-widget"]');
	
	            $menulink.each(function(){
	                var $el             = $(this),
	                    $listTitle          = $el.children('.menu-title'),
	                    $listSub            = $el.siblings('.collapse'),
	                    $listWidget         = $($el.attr('data-target')),
	                    $listWidgetParent   = ($listWidget.length)?$listWidget.parent():null,
	                    $popover            = null,
	                    $poptitle			= null,
	                    $popcontent         = null,
	                    $popoverSub         = null,
	                    popoverPosBottom    = 0,
	                    popoverCssBottom    = 0,
	                    elPadding           = $el.outerHeight() - $el.height()/4,
	                    listSubScroll       = false,
	                    elHasSub            = function(){
	                        if ($listWidget.length){
	                            $el.on('click', function(e){e.preventDefault()});
	                        }
	                        if ($listSub.length){
	                            //$listSub.removeClass('in').removeAttr('style');
	                            $el.on('click', function(e){e.preventDefault()}).parent('li').removeClass('active');
	                            return true;
	                        }else{
	                            return false;
	                        }
	                    }(),
	                    updateScrollInterval = null,
	                    updateScrollBar		 = function(el){
	                        clearInterval(updateScrollInterval);
	                        updateScrollInterval = setInterval(function(){
	                            el.nanoScroller({
	                                preventPageScrolling : true,
	                                alwaysVisible: true
	                            });
	                            clearInterval(updateScrollInterval);
	                        },100);
	                    };
	
	                $(document).click(function(event) {
	                    if(!$(event.target).closest('#mainnav-container').length) {
	                        $el.removeClass('hover').popover('hide');
	                    }
	                });
	
	                $('#mainnav-menu-wrap > .nano').on("update", function(event, values){
	                    $el.removeClass('hover').popover('hide');
	                });
	
	
	                $el.popover({
	                    animation       : false,
	                    trigger         : 'manual',
	                    container       : '#mainnav',
	                    viewport		: $el,
	                    html            : true,
	                    title           : function(){
	                        if (elHasSub) return $listTitle.html();
	                        return null
	                    },
	                    content         : function(){
	                        var $content;
	                        if (elHasSub){
	                            $content = $('<div class="sub-menu"></div>');
	                            $listSub.addClass('pop-in').wrap('<div class="nano-content"></div>').parent().appendTo($content);
	                        }else if($listWidget.length){
	                            $content = $('<div class="sidebar-widget-popover"></div>');
	                            $listWidget.wrap('<div class="nano-content"></div>').parent().appendTo($content);
	                        }else{
	                            $content = '<span class="single-content">' + $listTitle.html() + '</span>';
	                        }
	                        return $content;
	                    },
	                    template: '<div class="popover menu-popover"><h4 class="popover-title"></h4><div class="popover-content"></div></div>'
	                }).on('show.bs.popover', function () {
	                    if(!$popover){
	                        $popover = $el.data('bs.popover').tip();
	                        $poptitle = $popover.find('.popover-title');
	                        $popcontent = $popover.children('.popover-content');
	
	                        if (!elHasSub && $listWidget.length == 0)return;
	                        $popoverSub = $popcontent.children('.sub-menu');
	                    }
	                    if (!elHasSub && $listWidget.length == 0)return;
	                }).
	                on('shown.bs.popover', function () {
	                    if (!elHasSub && $listWidget.length == 0){
	                        var margintop = 0 - (0.5 * $el.outerHeight());
	                        $popcontent.css({'margin-top': margintop + 'px', 'width' : 'auto'});
	                        return;
	                    }
	
	
	                    var offsetTop 		= parseInt($popover.css('top')),
	                        elHeight		= $el.outerHeight(),
	                        offsetBottom 	= function(){
	                            if(flyContainer.hasClass('mainnav-fixed')){
	                                return $(window).outerHeight() - offsetTop - elHeight;
	                            }else{
	                                return $(document).height() - offsetTop - elHeight;
	                            }
	                        }(),
	                        popoverHeight	= $popcontent.find('.nano-content').children().css('height','auto').outerHeight();
	                    $popcontent.find('.nano-content').children().css('height','');
	
	
	
	                    if( offsetTop > offsetBottom){
	                        if($poptitle.length && !$poptitle.is(':visible')) elHeight = Math.round(0 - (0.5 * elHeight));
	                        offsetTop -= 5;
	                        $popcontent.css({'top': '','bottom': elHeight+'px', 'height': offsetTop}).children().addClass('nano').css({'width':'100%'}).nanoScroller({
	                            preventPageScrolling : true
	                        });
	                        updateScrollBar($popcontent.find('.nano'));
	                    }else{
	                        if(!flyContainer.hasClass('navbar-fixed') && flyMainNav.hasClass('affix-top')) offsetBottom -= 50;
	                        if(popoverHeight > offsetBottom){
	                            if(flyContainer.hasClass('navbar-fixed') || flyMainNav.hasClass('affix-top')) offsetBottom -= (elHeight + 5);
	
	                            offsetBottom -= 5;
	                            $popcontent.css({'top':elHeight+'px', 'bottom':'', 'height': offsetBottom}).children().addClass('nano').css({'width':'100%'}).nanoScroller({
	                                preventPageScrolling : true
	                            });
	
	                            updateScrollBar($popcontent.find('.nano'));
	                        }else{
	                            if($poptitle.length && !$poptitle.is(':visible')) elHeight = Math.round(0 - (0.5 * elHeight));
	                            $popcontent.css({'top':elHeight+'px', 'bottom':'', 'height': 'auto'});
	                        }
	                    }
	                    if($poptitle.length) $poptitle.css('height',$el.outerHeight());
	                    $popcontent.on('click', function(){
	                        $popcontent.find('.nano-pane').hide();
	                        updateScrollBar($popcontent.find('.nano'));
	                    });
	                })
	
	                    .on('click', function(){
	                    if(!flyContainer.hasClass('mainnav-sm')) return;
	                    $menulink.popover('hide');
	                    $el.addClass('hover').popover('show');
	                })
	                    .hover(
	                    function(){
	                        $menulink.popover('hide');
	                        $el.addClass('hover').popover('show').one('hidden.bs.popover', function () {
	                            // detach from popover, fire event then clean up data
	                            $el.removeClass('hover');
	                            if (elHasSub) {
	                                $listSub.removeAttr('style').appendTo($el.parent());
	                            }else if($listWidget.length){
	                                $listWidget.appendTo($listWidgetParent);
	                            }
	                            clearInterval(hidePopover);
	                        })
	                    },
	                    function(){
	                        clearInterval(hidePopover);
	                        hidePopover = setInterval(function(){
	                            if ($popover) {
	                                $popover.one('mouseleave', function(){
	                                    $el.removeClass('hover').popover('hide');
	                                });
	                                if(!$popover.is(":hover")){
	                                    $el.removeClass('hover').popover('hide');
	                                }
	                            };
	                            clearInterval(hidePopover);
	                        }, 100);
	                    }
	                );
	            });
	            isSmallNav = true;
	        },
	        unbindSmallNav = function(){
	            var colapsed = $('#mainnav-menu').find('.collapse');
	            if(colapsed.length){
	                colapsed.each(function(){
	                    var cl = $(this);
	                    if (cl.hasClass('in')){
	                        cl.parent('li').addClass('active');
	                    }else{
	                        cl.parent('li').removeClass('active');
	                    }
	                });
	            }
	            /*if(scrollbar != null && scrollbar.length){
	                scrollbar.nanoScroller({stop : true});
	            }*/
	
	            $menulink.popover('destroy').unbind('mouseenter mouseleave');
	            isSmallNav = false;
	        },
	        updateSize = function(){
	            //if(!defaultSize) return;
	
	            var sw = flyContainer.width(), currentScreen;
	
	
	            if (sw <= 740) {
	                currentScreen = 'xs';
	            }else if (sw > 740 && sw < 992) {
	                currentScreen = 'sm';
	            }else if (sw >= 992 && sw <= 1200 ) {
	                currentScreen = 'md';
	            }else{
	                currentScreen = 'lg';
	            }
	
	            if (screenCat != currentScreen){
	                screenCat = currentScreen;
	                screenSize = currentScreen;
	                if(screenSize == 'sm' && flyContainer.hasClass('mainnav-lg')){
	                    $.flyNav('collapse');
	                }else if(screenSize == "xs" && flyContainer.hasClass('mainnav-lg')){
	                    flyContainer.removeClass('mainnav-sm mainnav-out mainnav-lg').addClass('mainnav-sm');
	                }
	                else if(screenSize == "lg"){
	                    //$.flyNav('expand');
	                }
	            }
	        },
	        boxedPosition = function(){
	            if(flyContainer.hasClass('boxed-layout') && flyContainer.hasClass('mainnav-fixed') && boxedContainer.length){
	                flyMainNav.css({
	                    'left' : boxedContainer.offset().left + 'px'
	                });
	            }else{
	                flyMainNav.css({
	                    'left' :''
	                });
	            }
	        },
	        updateAffix = function(){
	            if(!ignoreAffix){
	                try{
	                    flyMainNav.flyAffix('update');
	                }catch(err){
	                    ignoreAffix = true;
	                }
	            }
	        },
	        updateNav = function(e){
	            updateAffix();
	
	            unbindSmallNav();
	            updateSize();
	            boxedPosition();
	
	            if (updateMethod == 'collapse' || flyContainer.hasClass('mainnav-sm') ) {
	                flyContainer.removeClass('mainnav-in mainnav-out mainnav-lg');
	                bindSmallNav();
	            }
	
	            mainNavHeight = $('#mainnav').height();
	            updateMethod = false;
	            return null;
	        },
	        init = function(){
	            if (!defaultSize) {
	                defaultSize = {
	                    xs : 'mainnav-out',
	                    sm : flyMainNav.data('sm') || flyMainNav.data('all'),
	                    md : flyMainNav.data('md') || flyMainNav.data('all'),
	                    lg : flyMainNav.data('lg') || flyMainNav.data('all')
	                }
	
	                var hasData = false;
	                for (var item in defaultSize) {
	                    if (defaultSize[item]) {
	                        hasData = true;
	                        break;
	                    }
	                }
	
	                if (!hasData) defaultSize = null;
	                updateSize();
	            }
	        },
	        methods = {
	            'revealToggle' : function(){
	                if (!flyContainer.hasClass('reveal')) flyContainer.addClass('reveal');
	                flyContainer.toggleClass('mainnav-in mainnav-out').removeClass('mainnav-lg mainnav-sm')
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'revealIn' : function(){
	                if (!flyContainer.hasClass('reveal')) flyContainer.addClass('reveal');
	                flyContainer.addClass('mainnav-in').removeClass('mainnav-out mainnav-lg mainnav-sm');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'revealOut' : function(){
	                if (!flyContainer.hasClass('reveal')) flyContainer.addClass('reveal');
	                flyContainer.removeClass('mainnav-in mainnav-lg mainnav-sm').addClass('mainnav-out');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'slideToggle' : function(){
	                if (!flyContainer.hasClass('slide')) flyContainer.addClass('slide');
	                flyContainer.toggleClass('mainnav-in mainnav-out').removeClass('mainnav-lg mainnav-sm')
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'slideIn' : function(){
	                if (!flyContainer.hasClass('slide')) flyContainer.addClass('slide');
	                flyContainer.addClass('mainnav-in').removeClass('mainnav-out mainnav-lg mainnav-sm');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'slideOut' : function(){
	                if (!flyContainer.hasClass('slide')) flyContainer.addClass('slide');
	                flyContainer.removeClass('mainnav-in mainnav-lg mainnav-sm').addClass('mainnav-out');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'pushToggle' : function(){
	                flyContainer.toggleClass('mainnav-in mainnav-out').removeClass('mainnav-lg mainnav-sm');
	                if (flyContainer.hasClass('mainnav-in mainnav-out')) flyContainer.removeClass('mainnav-in');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'pushIn' : function(){
	                flyContainer.addClass('mainnav-in').removeClass('mainnav-out mainnav-lg mainnav-sm');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'pushOut' : function(){
	                flyContainer.removeClass('mainnav-in mainnav-lg mainnav-sm').addClass('mainnav-out');
	                if(isSmallNav) unbindSmallNav();
	                return;
	            },
	            'colExpToggle' : function(){
	                if (flyContainer.hasClass('mainnav-lg mainnav-sm')) flyContainer.removeClass('mainnav-lg');
	                flyContainer.toggleClass('mainnav-lg mainnav-sm').removeClass('mainnav-in mainnav-out');
	                return flyWindow.trigger('resize');
	            },
	            'collapse' : function(){
	                flyContainer.addClass('mainnav-sm').removeClass('mainnav-lg mainnav-in mainnav-out');
	                updateMethod = 'collapse';
	                return flyWindow.trigger('resize');
	            },
	            'expand' : function(){
	                flyContainer.removeClass('mainnav-sm mainnav-in mainnav-out').addClass('mainnav-lg');
	                return flyWindow.trigger('resize');
	            },
	            'togglePosition' : function(){
	                flyContainer.toggleClass('mainnav-fixed');
	                updateAffix();
	            },
	            'fixedPosition' : function(){
	                flyContainer.addClass('mainnav-fixed');
	                scrollbar.nanoScroller({preventPageScrolling : true});
	                updateAffix();
	            },
	            'staticPosition' : function(){
	                flyContainer.removeClass('mainnav-fixed');
	                scrollbar.nanoScroller({preventPageScrolling : false});
	                updateAffix();
	            },
	            'update' : updateNav,
	            'refresh' : updateNav,
	            'getScreenSize' : function(){
	                return screenCat
	            },
	            'bind' : function(){
	                var menu = $('#mainnav-menu');
	                if (menu.length == 0) return false;
	
	                $menulink               = $('#mainnav-menu > li > a, #mainnav-menu-wrap .mainnav-widget a[data-toggle="menu-widget"]');
	                flyContainer          = $('#container');
	                boxedContainer          = flyContainer.children('.boxed');
	                flyMainNav            = $('#mainnav-container');
	                mainNavHeight           = $('#mainnav').height();
	
	                var transitionNav       = null;
	                flyMainNav.on('transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd', function(e){
	                    if(transitionNav || e.target === flyMainNav[0]){
	                        clearInterval(transitionNav);
	                        transitionNav = setInterval(function(){
	                            $(window).trigger('resize');
	                            clearInterval(transitionNav);
	                            transitionNav = null;
	                        }, 300);
	                    }
	                });
	
	
	                var toggleBtn = $('.mainnav-toggle');
	                if(toggleBtn.length){
	                    toggleBtn.on('click', function(e){
	                        e.preventDefault();
	                        e.stopPropagation();
	
	                        if(toggleBtn.hasClass('push')){
	                            $.flyNav('pushToggle');
	                        }else if(toggleBtn.hasClass('slide')){
	                            $.flyNav('slideToggle');
	                        }else if(toggleBtn.hasClass('reveal')){
	                            $.flyNav('revealToggle');
	                        }else{
	                            $.flyNav('colExpToggle');
	                        }
	                    }
	                )};
	
	
	                // COLLAPSIBLE MENU LIST
	                // =================================================================
	                // Require MetisMenu
	                // http://demo.onokumus.com/metisMenu/
	                // =================================================================
	                try {
	                    menu.metisMenu({ toggle: true });
	                }catch(err) {
	                    console.error(err.message);
	                }
	
	
	
	
	                // STYLEABLE SCROLLBARS
	                // =================================================================
	                // Require nanoScroller
	                // http://jamesflorentino.github.io/nanoScrollerJS/
	                // =================================================================
	                try {
	                    scrollbar = $('#mainnav-menu-wrap > .nano');
	                    if(scrollbar.length) scrollbar.nanoScroller({ preventPageScrolling : (flyContainer.hasClass('mainnav-fixed')?true:false)});
	                }catch(err) {
	                    console.error(err.message);
	                }
	
	                $(window).on('resizeEnd',updateNav).trigger('resize');
	            }
	        };
	
	
	    $.flyNav = function(method,complete){
	        if (methods[method]){
	            if(method == 'colExpToggle' || method == 'expand' || method == 'collapse'){
	                if(screenSize == 'xs' && method == 'collapse'){
	                    method = 'pushOut';
	                }else if((screenSize == 'xs' || screenSize == 'sm') && (method=='colExpToggle' || method == 'expand') && flyContainer.hasClass('mainnav-sm')){
	                    method = 'pushIn';
	                }
	            }
	            var val = methods[method].apply(this,Array.prototype.slice.call(arguments, 1));
	            if(method != 'bind') updateNav();
	            if(complete) return complete();
	            else if (val) return val;
	        }
	        return null;
	    };
	
	
	
	    $.fn.isOnScreen = function(){
	        var viewport = {
	            top : flyWindow.scrollTop(),
	            left : flyWindow.scrollLeft()
	        };
	        viewport.right = viewport.left + flyWindow.width();
	        viewport.bottom = viewport.top + flyWindow.height();
	
	        var bounds = this.offset();
	        bounds.right = bounds.left + this.outerWidth();
	        bounds.bottom = bounds.top + this.outerHeight();
	
	        return (!(viewport.right < bounds.left || viewport.left > bounds.right || viewport.bottom < bounds.bottom || viewport.top > bounds.top));
	    };
	
	};
	
	/* ========================================================================
	* Fly ASIDE v1.4.1
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.aside = function(){

	    var flyAside = null, flyContainer, flyWindow = $(window),
	        asideMethods = {
	            'toggleHideShow' : function(){
	                flyContainer.toggleClass('aside-in');
	                flyWindow.trigger('resize');
	                if(flyContainer.hasClass('aside-in')){
	                    toggleNav();
	                }
	            },
	            'show' : function(){
	                flyContainer.addClass('aside-in');
	                flyWindow.trigger('resize');
	                toggleNav();
	            },
	            'hide' : function(){
	                flyContainer.removeClass('aside-in');
	                flyWindow.trigger('resize');
	            },
	            'toggleAlign' : function(){
	                flyContainer.toggleClass('aside-left');
	                updateAside();
	            },
	            'alignLeft' : function(){
	                flyContainer.addClass('aside-left');
	                updateAside();
	            },
	            'alignRight' : function(){
	                flyContainer.removeClass('aside-left');
	                updateAside();
	            },
	            'togglePosition' : function(){
	                flyContainer.toggleClass('aside-fixed');
	                updateAside();
	            },
	            'fixedPosition' : function(){
	                flyContainer.addClass('aside-fixed');
	                updateAside();
	            },
	            'staticPosition' : function(){
	                flyContainer.removeClass('aside-fixed');
	                updateAside();
	            },
	            'toggleTheme' : function(){
	                flyContainer.toggleClass('aside-bright');
	            },
	            'brightTheme' : function(){
	                flyContainer.addClass('aside-bright');
	            },
	            'darkTheme' : function(){
	                flyContainer.removeClass('aside-bright');
	            },
	            'update' : function(){
	                updateAside();
	            },
	            'bind' : function(){
	                bindAside();
	            }
	        },
	        toggleNav = function(){
	            var sw = flyContainer.width();
	            if(flyContainer.hasClass('mainnav-in') && sw > 740){
	                if(sw > 740 && sw < 992){
	                    $.flyNav('collapse');
	                }else{
	                    flyContainer.removeClass('mainnav-in mainnav-lg mainnav-sm').addClass('mainnav-out');
	                }
	            }
	        },
	        boxedContainer = $('#container').children('.boxed'), navlg = 0, navsm = 0,
	        updateAside = function(){
	            try{
	                flyAside.flyAffix('update');
	            }catch(err){}
	            var cssProp = {};
	            if(flyContainer.hasClass('boxed-layout') && flyContainer.hasClass('aside-fixed') && boxedContainer.length){
	                if(flyContainer.hasClass('aside-left')){
	                    cssProp ={
	                        '-ms-transform' : 'translateX(' + boxedContainer.offset().left + 'px)',
	                        '-webkit-transform' : 'translateX(' + boxedContainer.offset().left + 'px)',
	                        'transform' : 'translateX(' + boxedContainer.offset().left + 'px)'
	                    }
	                }else{
	                    cssProp = {
	                        '-ms-transform' : 'translateX(' + ( 0 - boxedContainer.offset().left) + 'px)',
	                        '-webkit-transform' : 'translateX(' + (0 - boxedContainer.offset().left) + 'px)',
	                        'transform' : 'translateX(' + (0 - boxedContainer.offset().left) + 'px)'
	                    }
	                }
	            }else{
	                cssProp = {
	                    '-ms-transform' : '',
	                    '-webkit-transform' : '',
	                    'transform' : '',
	                    'right':''
	                }
	            }
	            flyAside.css(cssProp);
	        },
	        bindAside = function(){
	            flyAside = $('#aside-container');
	            if(flyAside.length){
	                flyContainer = $('#container');
	                flyWindow.on('resizeEnd',updateAside);
	                flyAside.on('transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd', function(e){
	                    if(e.target == flyAside[0]){
	                        flyWindow.trigger('resize');
	                    }
	                });
	
	
	                // STYLEABLE SCROLLBARS
	                // =================================================================
	                // Require nanoScroller
	                // http://jamesflorentino.github.io/nanoScrollerJS/
	                // =================================================================
	                flyAside.find('.nano').nanoScroller({preventPageScrolling : (flyContainer.hasClass('aside-fixed')?true:false)});
	
	                var toggleBtn = $('.aside-toggle');
	                if(toggleBtn.length){
	                    toggleBtn.on('click', function(e){
	                        e.preventDefault();
	                        $.flyAside('toggleHideShow');
	                    })
	                }
	            }
	        };
	
	    $.flyAside = function(method,complete){
	        if (asideMethods[method]){
	            asideMethods[method].apply(this,Array.prototype.slice.call(arguments, 1));
	            if(complete) return complete();
	        }
	        return null;
	    }
	};
	
	
	/* ========================================================================
	* MEGA DROPDOWN v1.2
	* ------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.megaDropdown = function() {
	    
	    var megadropdown = null,
	        mega = function(el){
	            var megaBtn = el.find('.mega-dropdown-toggle'), megaMenu = el.find('.mega-dropdown-menu');
	
	            megaBtn.on('click', function(e){
	                e.preventDefault();
	                el.toggleClass('open');
	            });
	        },
	        methods = {
	            toggle : function(){
	                this.toggleClass('open');
	                return null;
	            },
	            show : function(){
	                this.addClass('open');
	                return null;
	            },
	            hide : function(){
	                this.removeClass('open');
	                return null;
	            }
	        };
	
	    $.fn.flyMega = function(method){
	        var chk = false;
	        this.each(function(){
	            if(methods[method]){
	                chk = methods[method].apply($(this).find('input'),Array.prototype.slice.call(arguments, 1));
	            }else if (typeof method === 'object' || !method) {
	                mega($(this));
	            };
	        });
	        return chk;
	    };
	
	    //$(document).ready(function(){
	        megadropdown = $('.mega-dropdown');
	        if(megadropdown.length){
	            megadropdown.flyMega();
	            $('html').on('click', function(e) {
	                if (!$(e.target).closest('.mega-dropdown').length) {
	                    megadropdown.removeClass('open');
	                }
	            });
	        };
	    //});
	
	};
	
	/* ========================================================================
	* PANEL REMOVAL v1.1
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.panelRemoval = function () {
	    
	
	    $(document).ready(function(){
	        var closebtn = $('[data-dismiss="panel"]');
	
	        if (closebtn.length) {
	            closebtn.one('click', function(e){
	                e.preventDefault();
	                var el = $(this).parents('.panel');
	
	                el.addClass('remove').on('transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd', function(e){
	                    if (e.originalEvent.propertyName == "opacity") {
	                        el.remove();
	                    }
	                });
	            });
	        }else{closebtn = null}
	    });
	
	};
	
	
	/* ========================================================================
	* SCROLL TO TOP BUTTON v1.3
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.scrollToTop = function () {

		$(document).ready(function(){
	        var flyScrollTop = $('.scroll-top'), flyWindow = $(window), isMobile = function(){
	            return ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) )
	        }();
	        if (flyScrollTop.length && !isMobile) {
	            var isVisible = false;
	            var offsetTop = 250;
	            function calcScroll(){
	                if (flyWindow.scrollTop() > offsetTop && !isVisible) {
	                    flyScrollTop.addClass('in').stop(true, true).css({'animation':'none'}).show(0).css({
	                        "animation" : "jellyIn .8s"
	                    });
	                    isVisible = true;
	                }else if (flyWindow.scrollTop() < offsetTop && isVisible) {
	                    flyScrollTop.removeClass('in');
	                    isVisible = false;
	                }
	            };
	
	            calcScroll();
	            flyWindow.scroll(calcScroll);
	            flyScrollTop.on('click', function(e){
	                e.preventDefault();
	                $('body, html').animate({scrollTop : 0}, 500);
	            });
	        }else{
	            flyScrollTop = null;
	            flyWindow = null;
	        }
	        isMobile = null;
	    });
	};
	
	/* ========================================================================
	* Fly OVERLAY v1.1
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.overlay = function() {

	    var defaults = {
	        'displayIcon'	: true,
	        // DESC	 		: Should we display the icon or not.
	        // VALUE	 	: true or false
	        // TYPE 	 	: Boolean
	
	
	        'iconColor'		: 'text-dark',
	        // DESC	 		: The color of the icon..
	        // VALUE	 	: text-light || text-primary || text-info || text-success || text-warning || text-danger || text-mint || text-purple || text-pink || text-dark
	        // TYPE 	 	: String
	
	        'iconClass'		: 'fa fa-refresh fa-spin fa-2x',
	        // DESC  		: Class name of the font awesome icons", Currently we use font-awesome for default value.
	        // VALUE 		: (Icon Class Name)
	        // TYPE			: String
	
	
	        'title'			: '',
	        // DESC			: Overlay title
	        // TYPE			: String
	
	        'desc'			: ''
	        // DESC			: Descrition
	        // TYPE			: String
	
	
	    },
	    uID = function() {
	        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
	    },
	    methods = {
	        'show' : function(el){
	            var target = $(el.attr('data-target')),
	                ovId = 'fly-overlay-' + uID() + uID()+"-" + uID(),
	                panelOv = $('<div id="'+ ovId +'" class="panel-overlay"></div>');
	
	            el.prop('disabled', true).data('flyOverlay',ovId);
	            target.addClass('panel-overlay-wrap');
	            panelOv.appendTo(target).html(el.data('overlayTemplate'));
	            return null;
	        },
	        'hide': function(el){
	            var target = $(el.attr('data-target'));
	            var boxLoad = $('#'+ el.data('flyOverlay'));
	
	            if (boxLoad.length) {
	                el.prop('disabled', false);
	                target.removeClass('panel-overlay-wrap');
	                boxLoad.hide().remove();
	            }
	            return null;
	        }
	    },
	    loadBox = function(el,options){
	        if (el.data('overlayTemplate')) {
	            return null;
	        }
	        var opt = $.extend({},defaults,options),
	            icon = (opt.displayIcon)?'<span class="panel-overlay-icon '+opt.iconColor+'"><i class="'+opt.iconClass+'"></i></span>':'';
	        el.data('overlayTemplate', '<div class="panel-overlay-content pad-all unselectable">'+icon+'<h4 class="panel-overlay-title">'+opt.title+'</h4><p>'+opt.desc+'</p></div>');
	        return null;
	    };
	
	    $.fn.flyOverlay = function(method){
	        if (methods[method]){
	            return methods[method](this);
	        }else if (typeof method === 'object' || !method) {
	            return this.each(function () {
	                loadBox($(this), method);
	            });
	        }
	        return null;
	    };
	
	};
	
	
	
	/* ========================================================================
	* Fly LANGUAGE SELECTOR v1.1
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	*
	* REQUIRE BOOTSTRAP DROPDOWNS
	* http://getbootstrap.com/components/#dropdowns
	* ========================================================================*/
	
	T5Admin.langSelector = function() {

	    var defaults = {
	        'dynamicMode'       : true,
	        'selectedOn'        : null,
	        'onChange'          : null
	    },
	
	    langSelector = function(el, opt){
	        var options = $.extend({},defaults, opt );
	        var $el = el.find('.lang-selected'),
	            $langMenu = $el.parent('.lang-selector').siblings('.dropdown-menu'),
	            $langBtn = $langMenu.find('a'),
	            selectedID = $langBtn.filter('.active').find('.lang-id').text(),
	            selectedName = $langBtn.filter('.active').find('.lang-name').text();
	
	        var changeTo = function(te){
	            $langBtn.removeClass('active');
	            te.addClass('active');
	            $el.html(te.html());
	
	            selectedID = te.find('.lang-id').text();
	            selectedName = te.find('.lang-name').text();
	            el.trigger('onChange', [{id:selectedID, name : selectedName}]);
	
	
	
	            if(typeof options.onChange == 'function'){
	                options.onChange.call(this, {id:selectedID, name : selectedName});
	            }
	        };
	
	
	        $langBtn.on('click', function(e){
	            if (options.dynamicMode) {
	                e.preventDefault();
	                e.stopPropagation();
	            };
	
	            el.dropdown('toggle');
	            changeTo($(this));
	        });
	
	
	        if (options.selectedOn) changeTo( $(options.selectedOn) );
	
	    },
	    methods = {
	        'getSelectedID' : function(){
	            return $(this).find('.lang-id').text();
	        },
	        'getSelectedName' : function(){
	            return $(this).find('.lang-name').text();
	        },
	        'getSelected' :function(){
	            var el = $(this);
	            return {id:el.find('.lang-id').text() ,name:el.find('.lang-name').text()};
	        },
	        'setDisable' : function(){
	            $(this).addClass('disabled');
	            return null;
	        },
	        'setEnable' : function(el){
	            $(this).removeClass('disabled');
	            return null;
	        }
	    };
	
	    $.fn.flyLanguage = function(method){
	        var chk = false;
	        this.each(function(){
	            if(methods[method]){
	                chk = methods[method].apply(this,Array.prototype.slice.call(arguments, 1));
	            }else if (typeof method === 'object' || !method) {
	                langSelector($(this), method);
	            };
	        });
	        return chk;
	    }
	};
	
	/* ========================================================================
	* Fly NOTIFICATION v1.3
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.notification = function() {
	    var pageHolder, floatContainer = {}, notyContainer, flyContainer, flyContentContainer, addNew = false, supportTransition = function(){
	        var thisBody = document.body || document.documentElement,
	            thisStyle = thisBody.style,
	            support = thisStyle.transition !== undefined || thisStyle.WebkitTransition !== undefined;
	        return support;
	    }();
	    $.flyNoty = function(options){
	        var defaults = {
	            type        : 'primary',
	            // DESC     : Specify style for the alerts.
	            // VALUE    : primary || info || success || warning || danger || mint || purple || pink ||  dark
	            // TYPE     : String
	
	
	            icon        : '',
	            // DESC     : Icon class names
	            // VALUE    : (Icon Class Name)
	            // TYPE     : String
	
	
	            title       : '',
	            // VALUE    : (The title of the alert)
	            // TYPE     : String
	
	            message     : '',
	            // VALUE    : (Message of the alert.)
	            // TYPE     : String
	
	
	            closeBtn    : true,
	            // VALUE    : Show or hide the close button.
	            // TYPE     : Boolean
	
	
	
	            container   : 'page',
	            // DESC     : This option is particularly useful in that it allows you to position the notification.
	            // VALUE    : page || floating ||  "specified target name"
	            // TYPE     : STRING
	
	
	            floating    : {
	                position    : 'top-right',
	                // Floating position.
	                // Currently only supports "top-right". We will make further development for the next version.
	
	
	                animationIn : 'jellyIn',
	                // Please use the animated class name from animate.css
	
	                animationOut: 'fadeOut'
	                // Please use the animated class name from animate.css
	
	            },
	
	            html        : null,
	            // Insert HTML into the notification.  If false, jQuery's text method will be used to insert content into the DOM.
	
	
	            focus       : true,
	            //Scroll to the notification
	
	
	            timer       : 0,
	            // DESC     : To enable the "auto close" alerts, please specify the time to show the alert before it closed.
	            // VALUE    : Value is in milliseconds. (0 to disable the autoclose.)
	            // TYPE     : Number
	
	
	            //EVENTS / CALLBACK FUNCTIONS
	
	            onShow      : function(){},
	            // This event fires immediately when the show instance method is called.
	
	            onShown     : function(){},
	            // This event is fired when the modal has been made visible to the user (will wait for CSS transitions to complete).
	
	            onHide      : function(){},
	            // This event is fired immediately when the hide instance method has been called.
	
	            onHidden    : function(){}
	            // This event is fired when the notification has finished being hidden from the user (will wait for CSS transitions to complete).
	
	
	        },
	        opt = $.extend({},defaults, options ), el = $('<div class="alert-wrap"></div>'),
	        iconTemplate = function(){
	            var icon = '';
	            if (options && options.icon) {
	                icon = '<div class="media-left alert-icon"><i class="'+ opt.icon +'"></i></div>';
	            }
	            return icon;
	        },
	        alertTimer,
	        template = function(){
	            var clsBtn = opt.closeBtn ? '<button class="close" type="button"><i class="pci-cross pci-circle"></i></button>' : '';
	            var defTemplate = '<div class="alert alert-'+ opt.type + '" role="alert">'+ clsBtn + '<div class="media">';
	            if (!opt.html) {
	                return defTemplate + iconTemplate() + '<div class="media-body"><h4 class="alert-title">'+ opt.title +'</h4><p class="alert-message">'+ opt.message +'</p></div></div>';
	            }
	            return defTemplate + opt.html +'</div></div>';
	        }(),
	        closeAlert = function(e){
	            opt.onHide();
	            if (opt.container === 'floating' && opt.floating.animationOut) {
	                el.removeClass(opt.floating.animationIn).addClass(opt.floating.animationOut);
	                if (!supportTransition) {
	                    opt.onHidden();
	                    el.remove();
	                }
	            }
	
	            el.removeClass('in').on('transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd', function(e){
	                if (e.originalEvent.propertyName == "max-height") {
	                    opt.onHidden();
	                    el.remove();
	                }
	            });
	            clearInterval(alertTimer);
	            return null;
	        },
	        focusElement = function(pos){
	            $('body, html').animate({scrollTop: pos}, 300, function(){
	                el.addClass('in');
	            });
	        },
	        init = function(){
	            opt.onShow();
	            if (opt.container === 'page') {
	                if (!pageHolder) {
	                    pageHolder = $('<div id="page-alert"></div>');
	                    if(!flyContentContainer || !flyContentContainer.length) flyContentContainer = $('#content-container');
	                    flyContentContainer.prepend(pageHolder);
	                };
	
	                notyContainer = pageHolder;
	                if (opt.focus) focusElement(0);
	
	            }else if (opt.container === 'floating') {
	                if (!floatContainer[opt.floating.position]) {
	                    floatContainer[opt.floating.position] = $('<div id="floating-' + opt.floating.position + '" class="floating-container"></div>');
	                    if(!flyContainer || !flyContentContainer.length) flyContainer = $('#container');
	                    flyContainer.append(floatContainer[opt.floating.position]);
	                }
	
	                notyContainer = floatContainer[opt.floating.position];
	
	                if (opt.floating.animationIn) el.addClass('in animated ' + opt.floating.animationIn );
	                opt.focus = false;
	            }else {
	                var $ct =  $(opt.container);
	                var $panelct = $ct.children('.panel-alert');
	                var $panelhd = $ct.children('.panel-heading');
	
	                if (!$ct.length) {
	                    addNew = false;
	                    return false;
	                }
	
	
	                if(!$panelct.length){
	                    notyContainer = $('<div class="panel-alert"></div>');
	                    if($panelhd.length){
	                        $panelhd.after(notyContainer);
	                    }else{
	                        $ct.prepend(notyContainer)
	                    }
	                }else{
	                    notyContainer = $panelct;
	                }
	
	                if (opt.focus) focusElement($ct.offset().top - 30);
	
	            }
	            addNew = true;
	            return false;
	        }();
	
	        if (addNew) {
	            notyContainer.append(el.html(template));
	            el.find('[data-dismiss="noty"]').one('click', closeAlert);
	            if(opt.closeBtn) el.find('.close').one('click', closeAlert);
	            if (opt.timer > 0)alertTimer = setInterval(closeAlert, opt.timer);
	            if (!opt.focus) var addIn = setInterval(function(){el.addClass('in');clearInterval(addIn);},200);
	            el.one('transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd animationend webkitAnimationEnd oAnimationEnd MSAnimationEnd', function(e){
	                if ((e.originalEvent.propertyName == "max-height" || e.type == "animationend") && addNew) {
	                    opt.onShown();
	                    addNew = false;
	                }
	            });
	        }
	    };
	};
	
	/* ========================================================================
	* Fly CHECK v1.2
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.check = function() {

	    var allFormEl,
	    formElement = function(el){
	        if (el.data('fly-check')){
	            return;
	        }else{
	            el.data('fly-check', true);
	            if (el.text().trim().length){
	                el.addClass("form-text");
	            }else{
	                el.removeClass("form-text");
	            }
	        }
	
	
	        var input 	= el.find('input')[0],
	            groupName 	= input.name,
	            $groupInput	= function(){
	                if (input.type == 'radio' && groupName) {
	                    //return $('.form-radio').not(el).find('input').filter('input[name='+groupName+']').parent();
	                    return $('.form-radio').not(el).find('input').filter('input[name="' + groupName + '"]').parent();
	                }else{
	                    return false;
	                }
	            }(),
	            changed = function(){
	                if(input.type == 'radio' && $groupInput.length) {
	                    $groupInput.each(function(){
	                        var $gi = $(this);
	                        if ($gi.hasClass('active')) $gi.trigger('fly.ch.unchecked');
	                        $gi.removeClass('active');
	                    });
	                }
	
	
	                if (input.checked) {
	                    el.addClass('active').trigger('fly.ch.checked');
	                }else{
	                    el.removeClass('active').trigger('fly.ch.unchecked');
	                }
	            };
	
	        if (input.checked) {
	            el.addClass('active');
	        }else{
	            el.removeClass('active');
	        }
	
	        $(input).on('change', changed);
	    },
	    methods = {
	        isChecked : function(){
	            return this[0].checked;
	        },
	        toggle : function(){
	            this[0].checked = !this[0].checked;
	            this.trigger('change');
	            return null;
	        },
	        toggleOn : function(){
	            if(!this[0].checked){
	                this[0].checked = true;
	                this.trigger('change');
	            }
	            return null;
	        },
	        toggleOff : function(){
	            if(this[0].checked && this[0].type == 'checkbox'){
	                this[0].checked = false;
	                this.trigger('change');
	            }
	            return null;
	        }
	    },
	    bindForm = function(){
	        allFormEl = $('.form-checkbox, .form-radio');
	        if(allFormEl.length) allFormEl.flyCheck();
	    }
	
	    $.fn.flyCheck = function(method){
	        var chk = false;
	        this.each(function(){
	            if(methods[method]){
	                chk = methods[method].apply($(this).find('input'),Array.prototype.slice.call(arguments, 1));
	            }else if (typeof method === 'object' || !method) {
	                formElement($(this));
	            };
	        });
	        return chk;
	    };
	
	    $(document).on('fly.ready',bindForm).on('change', '.form-checkbox, .form-radio', bindForm).on('change', '.btn-file :file', function() {
	        var input = $(this),
	            numFiles = input.get(0).files ? input.get(0).files.length : 1,
	            label = input.val().replace(/\\/g, '/').replace(/.*\//, ''),
	            size = function(){
	                try{
	                    return input[0].files[0].size;
	                }catch(err){
	                    return 'Nan';
	                }
	            }(),
	            fileSize = function(){
	                if (size == 'Nan' ) {
	                    return "Unknown";
	                }
	                var rSize = Math.floor( Math.log(size) / Math.log(1024) );
	                return ( size / Math.pow(1024, rSize) ).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][rSize];
	            }();
	
	        input.trigger('fileselect', [numFiles, label, fileSize]);
	    });
	};
	
	
	/* ========================================================================
	* NAVIGATION SHORTCUT BUTTONS
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	* ========================================================================*/
	T5Admin.shortcutButtons = function(){
        var shortcutBtn = $('#mainnav-shortcut');
        if (shortcutBtn.length) {
            shortcutBtn.find('li').each(function () {
                var $el = $(this);
                $el.popover({
                    animation:false,
                    trigger: 'hover',
                    placement: 'bottom',
                    container: '#mainnav-container',
                    viewport: '#mainnav-container',
                    template: '<div class="popover mainnav-shortcut"><div class="arrow"></div><div class="popover-content"></div>'
                });
            });
        }else{
            shortcutBtn = null;
        }
	};
	
	
	/* ========================================================================
	* Fly AFFIX v1.3
	* -------------------------------------------------------------------------
	* Fly Exclusive Plugins - flywind.org
	* -------------------------------------------------------------------------
	*
	* PLEASE REMOVE THIS PLUGIN WHEN YOU DIDN'T USE THE NAVIGATION OR ASIDE WITH FIXED STATE.
	*
	*
	*
	* OPTIONAL PLUGINS.
	* You may choose whether to include it in your project or not.
	*
	*
	*
	* REQUIRE BOOTSTRAP AFFIX
	* http://getbootstrap.com/javascript/#affix
	* ========================================================================*/
	
	T5Admin.affix = function() {
	    
	
	    var flyMainNav, flyAside, flyContainer, flyMainnavScroll, flyAsideScroll, updateScrollInterval,
	    updateScroll = function(e){
	        clearInterval(updateScrollInterval);
	        updateScrollInterval = setInterval(function(){
	            if(e[0] == flyMainNav[0]){
	                flyMainnavScroll.nanoScroller({flash : true, preventPageScrolling: (flyContainer.hasClass('mainnav-fixed')?true:false)});
	            }else if(e[0] == flyAside[0]){
	                flyAsideScroll.nanoScroller({preventPageScrolling: (flyContainer.hasClass('aside-fixed')?true:false)});
	            }
	            clearInterval(updateScrollInterval);
	            updateScrollInterval = null;
	        }, 500);
	    },
	    bindAffix = function(){
	        flyContainer          = $('#container')
	        flyMainNav           	= $('#mainnav-container');
	        flyAside             	= $('#aside-container');
	        flyMainnavScroll      = $('#mainnav-menu-wrap > .nano');
	        flyAsideScroll        = $('#aside > .nano');
	
	        if (flyMainNav.length) flyMainNav.flyAffix({className : 'mainnav-fixed'});
	        if (flyAside.length) flyAside.flyAffix({className : 'aside-fixed'});
	    };
	
	    $.fn.flyAffix = function(method){
	        return this.each(function(){
	            var el = $(this), className;
	            if (typeof method === 'object' || !method){
	                className = method.className;
	                el.data('fly.af.class', method.className);
	            }else if (method == 'update') {
	                if(!el.data('fly.af.class')) bindAffix();
	                className = el.data('fly.af.class');
	                updateScroll(el);
	            }else if(method == 'bind'){
	                bindAffix();
	            }
	
	            if (flyContainer.hasClass(className) && !flyContainer.hasClass('navbar-fixed') ) {
	                el.affix({
	                    offset:{
	                        top:$('#navbar').outerHeight()
	                    }
	                }).on('affixed.bs.affix affix.bs.affix', function(){
	                    updateScroll(el);
	                });
	            }else if (!flyContainer.hasClass(className) || flyContainer.hasClass('navbar-fixed')) {
	                $(window).off(el.attr('id') +'.affix');
	                el.removeClass('affix affix-top affix-bottom').removeData('bs.affix');
	            }
	        });
	    }
	};

	return T5Admin;
	
}));