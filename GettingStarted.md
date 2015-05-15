#how to check out code and get the app running.

## Overview ##

This page gives an overview of how to get the app running from Eclipse. I haven't created a proper build and there is no compiled standalone RCP to download yet. We'll get there eventually.

## Eclipse ##

I wrote the code using _Eclipse for RCP/Plug-in Developers - Build id: 20090920-1017_. So downloading this release or a later one from the [Eclipse download site](http://www.eclipse.org/downloads/packages/) is a great start. Of course it may work on other versions of Eclipse, this is the only one I've tried so far.

## Check Out ##
You'll need to be able to check out the source code from Google's subversion repository for this hosted project. Eclipse boasts two subversion plugin sets or you may use any other subversion tools. I am currently using the subversive plugin set found [here](http://www.eclipse.org/subversive/).

## Launch ##
Once the three plugin projects are checked out you'll need a launch. Select the base UI project, **uk.co.dancowan.robots.ui**, and chose to run it as an application. You'll need to make a couple more changes to the launch:
  1. Add the SRV1 extension plugin to the base system (check the box for **uk.co.dancowan.robots.srv** on the plugins tab of the launch wizard)
  1. Change the startup details of the base UI plugin on the plugins tab of the launch wizard. Chose _autostart_ with a start level of 1.

I've tested this on a clean system and it works for me. If it doesn't work for you then please let me know and we can look into it.

Once the launch is ready you should be able to run the app out of Eclipse. It should start and show three views on the left hand third of the screen. Additional views (most notably the Camera View) can be found from the tool bar.

## Hardware ##
The other thing you'll need, if you _really_ want to play with the app, is a charged up SRV1. You can get one of these from the [Surveyor Corp](http://surveyor-corporation.stores.yahoo.net/index.html) or from one of the many global suppliers listed there. I live in the UK but with postage and sales tax I still found it cheaper to buy from the states, shop around!

Have fun.