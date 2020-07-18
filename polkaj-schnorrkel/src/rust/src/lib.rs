extern crate jni;
extern crate schnorrkel;
extern crate hex;
extern crate rand;

use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::{jbyteArray, jboolean};
use schnorrkel::{SecretKey, PublicKey, Signature, SignatureError};
use std::string::String;

const SIGNING_CTX: &'static [u8] = b"substrate";

fn sign(message: Vec<u8>, sk: Vec<u8>, pubkey: Vec<u8>) -> Result<Vec<u8>, String> {
    let pubkey = PublicKey::from_bytes(pubkey.as_slice())
        .map_err(|e| e.to_string())?;

    let signature = SecretKey::from_ed25519_bytes(sk.as_slice())
        .map_err(|e| e.to_string())?
        .sign_simple(SIGNING_CTX,
                     message.as_slice(),
                     &pubkey)
        .to_bytes()
        .to_vec();
    Ok(signature)
}

fn verify(signature: &[u8], message: &[u8], public: &[u8]) -> Result<bool, String> {
    let signature = Signature::from_bytes(signature)
        .map_err(|e| e.to_string())?;
    let result = PublicKey::from_bytes(public)
        .map_err(|e| e.to_string())?
        .verify_simple(SIGNING_CTX, message, &signature)
        .map(|_| true);
    match result {
        Ok(value) => Ok(value),
        Err(err) => match err {
            SignatureError::EquationFalse => Ok(false),
            _ => Err(err.to_string())
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_Schnorrkel_sign
    (env: JNIEnv, _class: JClass, pubkey: jbyteArray, sk: jbyteArray, message: jbyteArray) -> jbyteArray {

    let message = env.convert_byte_array(message)
        .expect("Message is not provided");
    let sk = env.convert_byte_array(sk)
        .expect("Secret Key is not provided");
    let pubkey = env.convert_byte_array(pubkey)
        .expect("Public Key is not provided");

    let output = match sign(message, sk, pubkey) {
        Ok(signature) => {
            env.byte_array_from_slice(signature.as_slice())
                .expect("Couldn't create result")
        },
        Err(msg) => {
            let none = env.new_byte_array(0)
                .expect("Couldn't create empty result");
            env.throw_new("io/emeraldpay/polkaj/schnorrkel/SchnorrkelException", msg).unwrap();
            none
        }
    };
    output
}

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_Schnorrkel_verify
(env: JNIEnv, _class: JClass, signature: jbyteArray, message: jbyteArray, pubkey: jbyteArray) -> jboolean {

    let message = env.convert_byte_array(message)
        .expect("Message is not provided");
    let pubkey = env.convert_byte_array(pubkey)
        .expect("Public Key is not provided");
    let signature = env.convert_byte_array(signature)
        .expect("Signature is not provided");

    let output = match verify(signature.as_slice(), message.as_slice(), pubkey.as_slice()) {
        Ok(valid) => {
            valid as jboolean
        },
        Err(msg) => {
            let none = false as jboolean;
            env.throw_new("io/emeraldpay/polkaj/schnorrkel/SchnorrkelException", msg).unwrap();
            none
        }
    };
    output
}