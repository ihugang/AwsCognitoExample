package com.example.awscognitosignup

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.example.awscognitosignup.ui.theme.AwsCognitoSignupTheme
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AwsCognitoSignupTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var userName by remember {mutableStateOf("")}
                    var email by remember {mutableStateOf("")}
                    var password by remember {mutableStateOf("")}
                    var code by remember { mutableStateOf("") }
                    var accesstoken by remember { mutableStateOf("") }
                    var myInfo by remember { mutableStateOf("") }

                    val keyboardController = LocalSoftwareKeyboardController.current

                    var fileUrl by remember {mutableStateOf("")}

                    val galleryLauncher =
                        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                            // process eith the received image uri
                            if (success) {
                                Log.d("FileUploader","file : $fileUrl")
                            }
                        }

                    Greeting("Android")

                    Column {
                        TextField(value = userName, onValueChange = { userName = it}, label = { Text(text = "请输入姓名") } )

                        TextField(value = email, onValueChange = { email = it}, label = { Text(text = "请输入电邮") })

                        TextField(value = password, onValueChange = { password = it}, label = { Text(text = "请输入密码，大于等于8位") })
                        
//                        Button(onClick = {
//                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
//                                // Do what you want with the error
//                                Log.d("register", error.toString())
//                            }
//                            GlobalScope.launch(exceptionHandler) {
//
//                               val r = CognitoHelper().signUp(userName,password, email)
//                               Log.d("register",r.toString())
//                           }
//
//                        }) {
//                            Text(text = "注册")
//                        }
//
//                        TextField(value = code, onValueChange = { code = it}, label = { Text(text = "邮箱验证码") } )
//
//                        Button(onClick = {
//                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
//                                // Do what you want with the error
//                                Log.d("register", error.toString())
//                            }
//                            GlobalScope.launch(exceptionHandler) {
//                                val r = CognitoHelper().confirmSignUp(code,email)
//                                Log.d("confirm",r.toString())
//                            }
//
//                        }) {
//                            Text(text = "验证")
//                        }
//
//
//                        Button(onClick = {
//                            keyboardController?.hide()
//
//                             val exceptionHandler = CoroutineExceptionHandler { context, error ->
//                                // Do what you want with the error
//                                Log.d("register", error.toString())
//                            }
//                            GlobalScope.launch(exceptionHandler) {
//                                val r = CognitoHelper().login(email,password)
//                                Log.d("register",r.toString())
//                                if (r.first) {
//                                    accesstoken = r.second
//                                }
//                            }
//
//                        }) {
//                            Text(text = "登录")
//                        }

//                        Button(onClick = {
//                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
//                                // Do what you want with the error
//                                Log.d("register", error.toString())
//                            }
//                            GlobalScope.launch(exceptionHandler) {
//                                val r = CognitoHelper().getMyInfo(accesstoken)
//                                Log.d("register",r.toString())
//                                myInfo = r.toString()
//                            }
//
//                        }) {
//                            Text(text = "获取个人信息")
//                        }
//
//                        Text(myInfo, maxLines = 4)

                        Button(onClick = {

                            fileUrl = getTempFile()
                            File(fileUrl).writeText("hello")
                            Log.d("upload",fileUrl)

                        }) {
                            Text("选择文件")
                        }

                        Button(onClick = {
                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
                                // Do what you want with the error
                                Log.d("upload", error.toString())
                            }
                            Log.d("upload","hello world.")
                            GlobalScope.launch(exceptionHandler) {
                                FileUploaderService().uploadFile(
                                    "5c8ddr2qudu8gfdloq7nf2k2eo",
                                    "0",
                                    fileUrl
                                ) {
                                    Log.d("result",it.toString())
                                }
                            }


                        }) {
                            Text("上传文件")
                        }
                    }
                }
            }
        }
    }

    //获取保存照片的Uri
    private fun getTempFile(): String {
        val storageFile: File? =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                externalCacheDir
            } else {
                cacheDir
            }

        val photoFile = File.createTempFile("tmp_image_file", ".txt", storageFile).apply {
            createNewFile()
            deleteOnExit()
        }
        return photoFile.path
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AwsCognitoSignupTheme {
        Greeting("Android")
    }
}