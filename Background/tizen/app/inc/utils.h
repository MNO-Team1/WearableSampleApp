#ifndef __UTILS_H__
#define __UTILS_H__

typedef struct _item_data {
	int index;
	Elm_Object_Item *item;
} item_data;

char *_gl_title_text_get(void *data, Evas_Object *obj, const char *part);
char *_gl_sub_title_text_get(void *data, Evas_Object *obj, const char *part);
char *_gl_main_text_get(void *data, Evas_Object *obj, const char *part);
void _gl_del(void *data, Evas_Object *obj);

#endif //__UTILS_H__
