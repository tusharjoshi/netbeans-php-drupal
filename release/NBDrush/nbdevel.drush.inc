<?php

function nbdevel_drush_command() {
  $items = array();

  // The 'make-me-a-sandwich' command
  $items['nb-hook'] = array(
    'description' => 'List implementations of a given hook and explore source of specified one.',
    'arguments' => array(
      'hook' => 'The name of the hook to explore.'
    ),
  );



  return $items;
}

/**
 * Command handler. Show hook implementations.
 */
function drush_nbdevel_nb_hook($hook) {
  // Get implementations in the .install files as well.
  include_once './includes/install.inc';
  drupal_load_updates();

  if ($hook_implementations = module_implements($hook)) {
	foreach ($hook_implementations as $impl){
		$output.=drush_nbdevel_fn_view($impl . "_$hook");
	}
	//return $output;
  }
  else {
    drush_log(dt('No implementations.'), 'ok');
  }
}

function drush_nbdevel_fn_view($function_name) {
  // Get implementations in the .install files as well.


  if (strpos($function_name, '::') === FALSE) {
    if (!function_exists($function_name)) {
      return drush_set_error(dt('Function not found'));
    }
    $reflect = new ReflectionFunction($function_name);
  }
  else {
    list($class, $method) = explode('::', $function_name);
    if (!method_exists($class, $method)) {
      return drush_set_error(dt('Method not found'));
    }
    $reflect = new ReflectionMethod($class, $method);
  }
  $func_info = array('!file' => $reflect->getFileName(), '!startline' => $reflect->getStartLine(), '!endline' => $reflect->getEndLine());
  //drush_print_pipe(dt("!file -line !startline", $func_info));
  drush_print_pipe($reflect->getFileName());
  drush_print(dt("<a href='file://!file:!startline' style='color:#0000ff;text-decoration:underline'>!file:!startline</a>", $func_info));

  //_drush_devel_print_function($reflect->getFileName(), $reflect->getStartLine(), $reflect->getEndLine());
}