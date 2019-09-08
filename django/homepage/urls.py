from django.urls import path

from .views import update_progress, get_latest_progress

urlpatterns = [
	path('update_progress', update_progress, name='update_progress'),
	path('get_latest_progress', get_latest_progress, name='get_latest_progress')
]
