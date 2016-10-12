#ifndef __HELLO_ACCESSORY_PROVIDER_H__
#define __HELLO_ACCESSORY_PROVIDER_H__

#include <app.h>
#include <glib.h>
#include <Elementary.h>
#include <system_settings.h>
#include <efl_extension.h>
#include <dlog.h>

#define TAG "WearableProvider"

#define NUM_OF_ITEMS 5

void initialize_sap();
gboolean send_data(char *message);
void update_ui(char *data);
gboolean find_peers(void);

#if !defined(PACKAGE)
#define PACKAGE "org.tizen.wearable"
#endif

#endif
