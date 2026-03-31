# Single DataWedge Profile Demo

a demo app to showcase how to use only one profile for the whole app, and switching scanner params for each page.

## The AAR

to quick demo how to call datawedge API, I am using following Zebra SDK wrapper
https://github.com/ys2714/zebra-sdk-kotlin-wrapper

## The Idea

for the use case seeking for super fast barcode scanner setup, and simplicity of app implement
we can limited the app to using only one profile. and use SWITCH_SCANNER_PARAMS API 
to config the scanner for each page

## The Limitation

the SWITCH_SCANNER_PARAMS API only able to change limited parameters.
for anything out of this API control, you need to create a new profile,
and switching between them to meet your business requirement.
