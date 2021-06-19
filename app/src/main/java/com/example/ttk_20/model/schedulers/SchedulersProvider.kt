package com.example.ttk_20.model.schedulers

import io.reactivex.Scheduler

interface SchedulersProvider {
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun trampoline(): Scheduler
    fun newThread(): Scheduler
    fun io(): Scheduler
}