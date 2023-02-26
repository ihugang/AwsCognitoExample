package com.example.awscognitosignup

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.awscognitosignup.ui.theme.AwsCognitoSignupTheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
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

                    Greeting("Android")

                    Column {
                        TextField(value = userName, onValueChange = { userName = it}, label = { Text(text = "请输入姓名") })

                        TextField(value = email, onValueChange = { email = it}, label = { Text(text = "请输入电邮") })

                        TextField(value = password, onValueChange = { password = it}, label = { Text(text = "请输入密码，大于等于8位") })
                        
                        Button(onClick = {
                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
                                // Do what you want with the error
                                Log.d("register", error.toString())
                            }
                            GlobalScope.launch(exceptionHandler) {

                               val r = CognitoHelper().signUp(userName,password, email)
                               Log.d("register",r.toString())
                           }

                        }) {
                            Text(text = "注册")
                        }

                        TextField(value = code, onValueChange = { code = it}, label = { Text(text = "邮箱验证码") })

                        Button(onClick = {
                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
                                // Do what you want with the error
                                Log.d("register", error.toString())
                            }
                            GlobalScope.launch(exceptionHandler) {
                                val r = CognitoHelper().confirmSignUp(code,email)
                                Log.d("confirm",r.toString())
                            }

                        }) {
                            Text(text = "验证")
                        }


                        Button(onClick = {
                            val exceptionHandler = CoroutineExceptionHandler { context, error ->
                                // Do what you want with the error
                                Log.d("register", error.toString())
                            }
                            GlobalScope.launch(exceptionHandler) {
                                val r = CognitoHelper().login(email,password)
                                Log.d("register",r.toString())
                            }

                        }) {
                            Text(text = "登录")
                        }
                    }
                }
            }
        }
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