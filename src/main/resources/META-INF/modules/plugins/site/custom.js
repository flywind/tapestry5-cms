/*
  * @package Spike
  * @subpackage Spike HTML
  * 
  * Template Scripts
  * Created by Tripples
  
   1.    Style Switcher
   2.    Navigation
   3.    Fixed Header
   4.    Main Slideshow (Carousel)
   5.    Counter
   6.    Owl Carousel
   7.    Flex Slider
   8.    Wow Animation
   9.    Contact Map
   10.   Video Background
   11.   Back To Top

  
*/


jQuery(function($) {
  "use strict";


   /* ----------------------------------------------------------- */
   /*  Style Switcher
   /* ----------------------------------------------------------- */

    (function($) { "use strict";
       $(document).ready(function(){
           $('.style-switch-button').click(function(){
           $('.style-switch-wrapper').toggleClass('active');
           });
           $('a.close-styler').click(function(){
           $('.style-switch-wrapper').removeClass('active');
           });
      });
    })(jQuery);


    /* ----------------------------------------------------------- */
  /*  Header
  /* ----------------------------------------------------------- */

   $(function(){
      
      $('a.page-scroll').bind('click', function(event) {
         var $anchor = $(this);
         $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top -62
         }, 1000, 'easeInOutExpo');
         event.preventDefault();
      });
      $('body').scrollspy({ target: '#header', offset:72 });
   });

   if (window.location.hash){
     $('html, body').stop().animate({
      scrollTop: $(window.location.hash).offset().top -62
    }, 1000, 'easeInOutExpo');
   }




   /* ----------------------------------------------------------- */
   /*  Fixed header
   /* ----------------------------------------------------------- */

   $(window).on('scroll', function(){

        if( $(window).scrollTop()>100 ){

        $('.header').addClass('header-solid animated fadeInDown');
        } 
        else {

        $('.header').removeClass('header-solid animated fadeInDown');

        }

    }); 

   $(window).on('scroll', function(){

        if( $(window).scrollTop()>200 ){

        $('.header2').addClass('header-bgnone animated fadeInDown');
        } 
        else {

        $('.header2').removeClass('header-bgnone animated fadeInDown');

        }

    }); 


   
  /* ----------------------------------------------------------- */
  /*  Main slideshow
  /* ----------------------------------------------------------- */

   $('#main-slide').carousel({
      pause: true,
      interval: 100000,
   });


   /* ----------------------------------------------------------- */
   /*  Counter
   /* ----------------------------------------------------------- */

      $('.counter').counterUp({
       delay: 10,
       time: 1000
      });



  /* ----------------------------------------------------------- */
  /*  Owl Carousel
  /* ----------------------------------------------------------- */


    //Testimonial

    $("#testimonial-carousel").owlCarousel({
 
      navigation : false, // Show next and prev buttons
      slideSpeed : 600,
      pagination:true,
      singleItem:true
 
    });

    // Custom Navigation Events
    var owl = $("#testimonial-carousel");


    // Custom Navigation Events
    $(".next").click(function(){
      owl.trigger('owl.next');
    })
    $(".prev").click(function(){
      owl.trigger('owl.prev');
    })
    $(".play").click(function(){
      owl.trigger('owl.play',1000); //owl.play event accept autoPlay speed as second parameter
    })
    $(".stop").click(function(){
      owl.trigger('owl.stop');
    })
    

   //Clients

   $("#client-carousel").owlCarousel({

      navigation : false, // Show next and prev buttons
      slideSpeed : 400,
      pagination:false,
      items : 5,
      rewindNav: true,
      itemsDesktop : [1199,3],
      itemsDesktopSmall : [979,3],
      stopOnHover:true,
      autoPlay:true

   });

   //App gallery
   $("#app-gallery-carousel").owlCarousel({

     navigation : false, // Show next and prev buttons
      slideSpeed : 400,
      pagination:true,
      items : 4,
      rewindNav: true,
      itemsDesktop : [1199,3],
      itemsDesktopSmall : [979,3],
      stopOnHover:true
   });



   /* ----------------------------------------------------------- */
   /*  Flex slider
   /* ----------------------------------------------------------- */

      //Second item slider
      $(window).load(function() {
    	  alert(1);
        $('.flexSlideshow').flexslider({
           animation: "fade",
           controlNav: false,
           directionNav: true ,
           slideshowSpeed: 8000
        });
      });


     //Portfolio item slider
      $(window).load(function() {
    	  alert(2);
        $('.flexportfolio').flexslider({
           animation: "fade",
           controlNav: false,
           directionNav: true ,
           slideshowSpeed: 8000
        });
      });

     
   /* ----------------------------------------------------------- */
   /*  Animation
   /* ----------------------------------------------------------- */
        //Wow
        new WOW().init();


   /* ----------------------------------------------------------- */
   /*  Prettyphoto
   /* ----------------------------------------------------------- */

        $("a[data-rel^='prettyPhoto']").prettyPhoto();


/* ----------------------------------------------------------- */
   /*  Contact map
   /* ----------------------------------------------------------- */

      /*$("#map").gmap3({
        map:{
            options:{
               center:[-37.8152065,144.963937],
               zoom: 14,
               scrollwheel: false
            }
        },
        marker:{
          values:[
            {address:"Corner Swanston St & Flinders St, Melbourne VIC 3000, Australia", data:" Welcome To bizCraft ! ! ", 
             options:{icon: "http://themewinter.com/html/marker.png"}}
          ],
          options:{
            draggable: false
          },
          events:{
            mouseover: function(marker, event, context){
              var map = $(this).gmap3("get"),
                infowindow = $(this).gmap3({get:{name:"infowindow"}});
              if (infowindow){
                infowindow.open(map, marker);
                infowindow.setContent(context.data);
              } else {
                $(this).gmap3({
                  infowindow:{
                    anchor:marker, 
                    options:{content: context.data}
                  }
                });
              }
            },
            mouseout: function(){
              var infowindow = $(this).gmap3({get:{name:"infowindow"}});
              if (infowindow){
                infowindow.close();
              }
            }
          }
        }
      });*/

   /* ----------------------------------------------------------- */
   /* Video background
   /* ----------------------------------------------------------- */

    var resizeVideoBackground = function() {

        $( '.video-background' ).each(function( i, el ) {
          var $el       = $( el ),
              $section  = $el.parent(),
              min_w     = 300,
              video_w   = 16,
              video_h   = 9,
              section_w = $section.outerWidth(),
              section_h = $section.outerHeight(),
              scale_w   = section_w / video_w,
              scale_h   = section_h / video_h,
              scale     = scale_w > scale_h ? scale_w : scale_h,
              new_video_w, new_video_h, offet_top, offet_left;


          if ( scale * video_w < min_w ) {
            scale = min_w / video_w;
          };

          new_video_w = scale * video_w;
          new_video_h = scale * video_h;
          offet_left = ( new_video_w - section_w ) / 2 * -1;
          offet_top  = ( new_video_h - section_h ) / 2 * -1;

          $el.css( 'width', new_video_w );
          $el.css( 'height', new_video_h );
          $el.css( 'marginTop', offet_top );
          $el.css( 'marginLeft', offet_left );
        });

    };

    $( window ).on( 'resize', function() {
        resizeVideoBackground();
    });
    
    resizeVideoBackground();

   /* ----------------------------------------------------------- */
   /*  Back to top
   /* ----------------------------------------------------------- */

       $(window).scroll(function () {
            if ($(this).scrollTop() > 50) {
                $('#back-to-top').fadeIn();
            } else {
                $('#back-to-top').fadeOut();
            }
        });
      // scroll body to 0px on click
      $('#back-to-top').click(function () {
          $('#back-to-top').tooltip('hide');
          $('body,html').animate({
              scrollTop: 0
          }, 800);
          return false;
      });
      
      $('#back-to-top').tooltip('hide');

});