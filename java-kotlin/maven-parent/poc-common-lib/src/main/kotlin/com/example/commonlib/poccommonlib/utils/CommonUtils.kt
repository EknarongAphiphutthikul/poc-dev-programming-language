package com.example.commonlib.poccommonlib.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun getLogger(c: () -> Unit): Logger = LoggerFactory.getLogger(c.javaClass.enclosingClass)