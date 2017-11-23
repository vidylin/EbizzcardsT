#include "com_github_xgfjyw_libnettools_NetSelect.h"
#include <android/log.h>

#include <assert.h>
#include <errno.h>
#include <stdlib.h>
#include <math.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/poll.h>
#include <arpa/inet.h>


#define THIS_APP "NetSelect"
#define PRINT(...)  __android_log_print(ANDROID_LOG_INFO, THIS_APP, __VA_ARGS__)


/*#define JNIREG_CLASS "com/github/xgfjyw/libnettools/NetSelect"//指定要注册的类
static void stop(JNIEnv*, jclass);


static JNINativeMethod gMethods[] = {
        { "test", "()V", (void*)run },
        //{"run", "(Landroid/content/Context;Ljava/lang/Class;)V", (void*)run2},
        { "stop", "()V", (void*)stop },
        { "setUID", "(I)V", (void*)setUID },
};

static int registerNativeMethods(JNIEnv* env, const char* className, JNINativeMethod* gMethods, int numMethods) {
    jclass clazz = (*env)->FindClass(env, className);
    if (clazz == NULL)
        return JNI_FALSE;

    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0)
        return JNI_FALSE;

    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void* reserved) {
    JNIEnv* env = NULL;
    jint jni_version = JNI_VERSION_1_6;

    if ((*vm)->GetEnv(vm, (void**)&env, jni_version) != JNI_OK)
        return -1;
    assert(env != NULL);

    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods, sizeof(gMethods) / sizeof(gMethods[0])))
        return -1;



    struct sigaction handler;
    memset(&handler, 0, sizeof(struct sigaction));
    handler.sa_sigaction = android_sigaction;
    handler.sa_flags = SA_RESETHAND;
    sigaction(SIGQUIT, &handler, &old_sa[SIGQUIT]);
    PRINT("installed action catcher");

    return jni_version;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    PRINT("JNI_OnUnload");
}*/

#define MAX_TTL     28
#define MIN_TTL     1
#define TRIES       3
#define MAXDATASIZE 64
#define TIMEOUT     3
#define KEY_LEN     7

static double gettime();
static const char* random_string_with_length(int);
static double score(double, int, int, int);


struct result_table {
    char key[KEY_LEN + 1];
    int cnt, ttl;
    double start, end;
};

static void store_start_data(struct result_table *table, int table_len, const char *key, int ttl, int cnt, double time) {
    int i;
    struct result_table *p;
    for (i = 0; i < table_len; i++) {
        p = &table[i];
        if (strlen(p->key) == 0)
            break;

    }

    strcpy(p->key, key);
    p->cnt = cnt;
    p->ttl = ttl;
    p->start = time;
}

static void store_end_data(struct result_table *table, int table_len, const char* key, double time) {
    int i;
    struct result_table *p;
    for (i = 0; i < table_len; i++) {
        p = &table[i];
        if (strlen(p->key) != 0 && strcmp(p->key, key) == 0) {
            if (i < table_len)
                p->end = time;
            else
                PRINT("cannot find corresponding key");

            break;
        }
    }
}

JNIEXPORT jint JNICALL Java_com_github_xgfjyw_libnettools_NetSelect_test(JNIEnv *env, jobject obj, jstring host, jint port) {

    int fd, ttl, count, loc;
    long nr;
    struct result_table table[MAX_TTL * TRIES];
    bzero(&table, sizeof(struct result_table) * MAX_TTL * TRIES);

    char recvbuf[MAXDATASIZE];
    struct sockaddr_in server; /* server's address information */
    socklen_t len;
    struct pollfd pollfds[1];
    double last_recvd;

    loc = 0;
    ttl = MAX_TTL;
    count = 1;
    last_recvd = gettime();
    len = sizeof(struct sockaddr_in);

    const char *seed = random_string_with_length(MAX_TTL * TRIES + KEY_LEN);


    /* create a new udp socket */
    if ((fd=socket(AF_INET, SOCK_DGRAM, 0)) == -1) {
        printf("socket() error\n");
        free(seed);
        return -1;
    }

    bzero(&server,sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    server.sin_addr.s_addr = inet_addr((*env)->GetStringUTFChars(env, host, 0));
    pollfds[0].fd = fd;
    pollfds[0].events = POLLIN;

    while (1) {
        if (ttl > MIN_TTL) {
            if (count > TRIES) {
                ttl--;
                count = 1;

                if (-1 == setsockopt(fd, IPPROTO_IP, IP_TTL, &ttl, sizeof(int))) {
                    PRINT("setsockopt error: %i", errno);
                    free(seed);
                    close(fd);
                    return -1;
                }
            }

            char key[KEY_LEN + 1];
            memcpy(key, &seed[loc], KEY_LEN);
            key[KEY_LEN] = '\0';
            int ret = sendto(fd, key, KEY_LEN, 0, (struct sockaddr *)&server, len);
            if (ret < 0)
                PRINT("sendto errno: %i", errno);
//            else
//                PRINT("send %s to %s:%i, %i", key, (*env)->GetStringUTFChars(env, host, 0), port, ret);

            store_start_data(table, MAX_TTL * TRIES, key, ttl, count, gettime());
            loc++;
            count++;
        }

        int nfound;
        while ((nfound = poll(pollfds, 1, 0)) != 0) {
            if (nfound < 0) {
                PRINT("poll() error: %i", errno);
                free(seed);
                close(fd);
                return -1;
            }
            if (pollfds[0].revents & (POLLIN | POLLERR)) {
                if ((nr = recvfrom(fd,recvbuf,MAXDATASIZE,0,(struct sockaddr *)&server,&len)) == -1) {/* calls recvfrom() */
                    PRINT("recvfrom() error: %i", errno);
                }
                last_recvd = gettime();

                recvbuf[nr]='\0';
                store_end_data(table, MAX_TTL * TRIES, recvbuf, gettime());
            }
        }


        if (gettime() - last_recvd > TIMEOUT) {
            break;
        } else {
            usleep(6 * 1000); //6ms
        }
    }

    free(seed);
    close(fd); /* close fd */

    int i, recvd, min_ttl;
    double start, end;
    recvd = 0;
    min_ttl = MAX_TTL;
    start = end = 0;

    for (i = 0; i < MAX_TTL * TRIES; i++) {
        struct result_table *p = &table[i];
        if (p->end > 0) {
            if (p->ttl < min_ttl)
                min_ttl = p->ttl;

            start += p->start;
            end += p->end;
            recvd++;
        }
    }

    double _score =  score((end - start)/recvd, (MAX_TTL - min_ttl + 1) * TRIES, recvd, min_ttl);
    PRINT("%s -> ttl = %d, recvd %i/%i, delay = %lf, score: %lf",
          (*env)->GetStringUTFChars(env, host, 0),
          min_ttl == MAX_TTL ? -1 : min_ttl,
          recvd,
          (MAX_TTL - min_ttl + 1) * TRIES,
          (end - start)/recvd,
          _score);

    return _score;
}

static double score(double lag, int numout, int numin, int hop) {
    double score, loss_rate;
    if (numin == 0)
        return 9999999;

    loss_rate = (numout - numin) / numout;
    score = (loss_rate * 100 + 1) * (loss_rate * 100 + 1);
    score = score * sqrt(lag * 1000);
    score = score * hop;

    return score;
}

static double gettime() {
    double time;

    struct timeval now;
    gettimeofday(&now, NULL);
    time = now.tv_sec + (double)now.tv_usec / 1000000;
    assert(time > 0);

    return time;
}

JNIEXPORT jstring JNICALL Java_com_github_xgfjyw_libnettools_NetSelect_randomString(JNIEnv *env, jobject obj, jint len) {
    const char *cstring = random_string_with_length(len);
    jstring str = (*env)->NewStringUTF(env, cstring);
    free(cstring);

    return str;
}

static const char* random_string_with_length(int len) {
    char *letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    char *random_string = malloc(len * sizeof(char) + 1);

    int i;
    int letters_len = strlen(letters);
    srand((int)(gettime() * 1000));
    for (i = 0; i < len; i++) {
        int random_number = floor(letters_len * (rand()/(RAND_MAX + 1.0f)));
        random_string[i] = letters[random_number];
    }
    random_string[len] = '\0';

    return random_string;
}