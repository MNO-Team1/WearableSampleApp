#include <efl_extension.h>

typedef struct _item_data {
	int index;
	Elm_Object_Item *item;
} item_data;

char *_gl_title_text_get(void *data, Evas_Object *obj, const char *part)
{
	char buf[1024];

	snprintf(buf, 1023, "%s", "LPA Demo");

	return strdup(buf);
}

char *_gl_sub_title_text_get(void *data, Evas_Object *obj, const char *part)
{
	char buf[1024];

	snprintf(buf, 1023, "%s", "Tizen");

	return strdup(buf);
}

char *main_menu_names[] = {
	"Get Device Info.",
	NULL
};

char *_gl_main_text_get(void *data, Evas_Object *obj, const char *part)
{
	char buf[1024];
	item_data *id = data;
	int index = id->index;

	if (!strcmp(part, "elm.text"))
		snprintf(buf, 1023, "%s", main_menu_names[index - 1]);

	return strdup(buf);
}

void _gl_del(void *data, Evas_Object *obj)
{
	// FIXME: Unrealized callback can be called after this.
	// Accessing Item_Data can be dangerous on unrealized callback.
	item_data *id = data;
	if (id) free(id);
}
