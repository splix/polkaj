extern crate jni;

use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::{jbyteArray, jstring};

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_Schnorrkel_sign
    (env: JNIEnv, _class: JClass, message: jbyteArray, sk: jbyteArray) -> jstring {

    let message = env.convert_byte_array(message)
        .expect("Message is not provided");
    let sk = env.convert_byte_array(sk)
        .expect("Secret Key is not provided");

    let output = env
        .new_string(format!("Hello, {}!", message.len() + sk.len()))
        .expect("Couldn't create java string!");
    output.into_inner()
}