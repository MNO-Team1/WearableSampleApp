#ifndef __MAIN_H__
#define __MAIN_H__

#include <app.h>
#include <glib.h>
#include <Elementary.h>
#include <system_settings.h>
#include <efl_extension.h>
#include <dlog.h>

#define TAG "LpaProvider"

#define NUM_OF_ITEMS 5


gboolean send_data(char *message);
void update_ui(char *data);
gboolean find_peers(void);

#if !defined(PACKAGE)
#define PACKAGE "org.tizen.lpademo"
#endif

#endif //__MAIN_H__
