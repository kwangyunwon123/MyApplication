#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <android/log.h>
#include <__locale>

using namespace cv;
using namespace std;

void overlayImage(const Mat &background, const Mat &foreground ,Mat &output, Point2i location)
{
    background.copyTo(output);
    for (int y = std::max(location.y, 0); y < background.rows; ++y)
    {
        int fY = y - location.y; // because of the translation
        if (fY >= foreground.rows){
            break;
        }
        for (int x = std::max(location.x, 0); x < background.cols; ++x)
        {
            int fX = x - location.x; // because of the translation.
            if (fX >= foreground.cols){
                break;
            }
            double opacity =
                    ((double)foreground.data[fY * foreground.step + fX * foreground.channels() + 3])/ 255.;
            for (int c = 0; opacity > 0 && c < output.channels(); ++c)
            {
                unsigned char foregroundPx =
                        foreground.data[fY * foreground.step + fX * foreground.channels() + c];
                unsigned char backgroundPx =
                        background.data[y * background.step + x * background.channels() + c];
                output.data[y*output.step + output.channels()*x + c] =
                        backgroundPx * (1. - opacity) + foregroundPx * opacity;
            }
        }
    }
}

float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_myapplication_CameraActivity_loadCascade(JNIEnv *env, jobject instance,
                                                     jstring cascadeFileName_) {
    //const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();
    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ","CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ","CascadeClassifier로 로딩 성공 %s", nativeFileNameString);
    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);
    return ret;
    // TODO

    //env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication_CameraActivity_detect(JNIEnv *env, jobject instance,
                                                jlong cascadeClassifier_face,
                                                jlong cascadeClassifier_eye, jlong matAddrInput,
                                                jlong matAddrResult, jlong third_variable) {
    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;
    Mat &img_sunglasses = *(Mat *) third_variable;


    img_result = img_input.clone();
    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);
    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 240);

    //-- Detect faces

    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(50, 50) );
    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",(char *) "face %d found ", faces.size());


    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);
        //ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,

        //Scalar(255, 0, 255), 30, 8, 0);

        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,real_facesize_height);
        Mat faceROI = img_gray( face_area );
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CASCADE_SCALE_IMAGE, Size(25, 25) );
        Mat resized_glasses;
        resize(img_sunglasses, resized_glasses, cv::Size( 70, 70));

        for ( size_t j = 0; j < eyes.size(); j++ )
        {
            Point eye_center( real_facesize_x + eyes[j].x + eyes[j].width/2 - 25, real_facesize_y + eyes[j].y + eyes[j].height/2 - 30);

            //int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
            //circle( img_result, eye_center, 5, Scalar( 255, 0, 0 ), 2, 8, 0 );
            if(j < eyes.size()-1)
                overlayImage(img_input,resized_glasses, img_input, eye_center);
            else
                overlayImage(img_input,resized_glasses, img_result, eye_center);


        }
    }


    // TODO

}