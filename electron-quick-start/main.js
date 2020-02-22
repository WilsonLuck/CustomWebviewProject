// Modules to control application life and create native browser window
const {
  app,
  BrowserWindow
} = require('electron')
const path = require('path')

function createWindow() {
  //createSplashWindows
  let spw = new BrowserWindow({
    width: 600,
    height: 400,
    frame: false,
    transparent: true,
    type: "splash",
    alwaysOnTop: true,
    show: false,
    position: "center",
    resizable: false,
    toolbar: false,
    fullscreen: false
  })

  spw.loadFile('./splash.html');
  spw.webContents.on('did-finish-load', function () {
    spw.show(); //close splash
  });
  // Emitted when the window is closed.
  spw.on('closed', () => {
    // Dereference the window object
    spw = null
  })
  // Create the browser window.
  let mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js')
    },
    show: false,
  })

  // and load the index.html of the app.
  mainWindow.loadFile('./app/pages/devices.html')

  // Open the DevTools.
  // mainWindow.webContents.openDevTools()
  mainWindow.webContents.on('did-finish-load', function () {
    setTimeout(() => {
      spw.close(); //close splash
      mainWindow.show(); //show main
    }, 2000);
  });
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function () {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') app.quit()
})

app.on('activate', function () {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (BrowserWindow.getAllWindows().length === 0) createWindow()
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.