# Cordova opensmile Plugin

Plugin that create a csv file and store the output of opensmile tool for audio recordings.

This plugin can be used to get live output from oopensmile tool. It will create a csv file and store it in the specified location in the device.

## Using

Create a new Cordova Project

    $ cordova create opensmile com.example.opensmileapp OpenSmile
    
Install the plugin

    $ cd opensmile
    $ cordova platform add android
    $ cordova plugin add https://github.com/nithinth7/plugin-opensmile.git

Change the android-targetSdkVersion to 22 in the config.xml if possible (otherwise you have to explicitly ask for permissions to record audio and to write to disk):

```xml

<widget ...>
    ...
    <platform name="android">
	...
	<preference name="android-targetSdkVersion" value="22" />
    </platform>
    ...
</widget>
```

Deploy the app on Android:
    
    $ cordova run android

Edit `www/js/index.js` and add the following code inside `onDeviceReady`

```js
    var success = function(message) {
        alert(message);
    }

    var failure = function() {
        alert("Error calling OpenSmile Plugin");
    }
```

### To start opensmile
```js
    opensmile.start(fname, fpath, success, failure);
```	
	Params:
		fname: name of the csv file to store opensmile output.
		fpath: path of the csv file to store in the device.
		success: success callback.
		failure: failure callback.

### To stop opensmile
```js	
	opensmile.stop("Stop", success, failure);
```	
	Params:
		success: success callback.
		failure: failure callback.
		
Install Android platform

    cordova platform add android
    
Run the code

    cordova run 

## More Info

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/latest/guide/cli/index.html)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html)
